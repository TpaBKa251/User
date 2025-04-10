package ru.tpu.hostel.user.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.user.dto.request.SessionLoginDto;
import ru.tpu.hostel.user.dto.response.SessionRefreshResponse;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;
import ru.tpu.hostel.user.entity.Session;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.common.exception.AccessException;
import ru.tpu.hostel.user.common.exception.IncorrectLogin;
import ru.tpu.hostel.user.common.exception.SessionNotFound;
import ru.tpu.hostel.user.jwt.JwtService;
import ru.tpu.hostel.user.repository.SessionRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.SessionService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public SessionResponseDto login(SessionLoginDto sessionLoginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(sessionLoginDto.email())
                .filter(user1 -> passwordEncoder.matches(sessionLoginDto.password(), user1.getPassword()))
                .orElseThrow(IncorrectLogin::new);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Session session = new Session();
        session.setUserId(user);
        session.setCreateTime(LocalDateTime.now());
        session.setExpirationTime(LocalDateTime.now().plusDays(30));
        session.setRefreshToken(refreshToken);

        sessionRepository.save(session);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new SessionResponseDto(session.getId(), accessToken);
    }


    @Override
    public ResponseEntity<?> logout(UUID sessionId, UUID userId, HttpServletResponse response) {
        // TODO: При логауте слать уведам сообщение по рэббиту об удалении токена уведов
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(SessionNotFound::new);

        if (!session.getUserId().getId().equals(userId)) {
            throw new AccessException("Вы не можете выйти из чужой сессии");
        }

        session.setRefreshToken(null);

        sessionRepository.save(session);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @Override
    public SessionRefreshResponse refresh(String refreshToken, HttpServletResponse response) {
        jwtService.checkRefreshTokenValidity(refreshToken);
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(SessionNotFound::new);

        if (session.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new AccessException("Сессия уже потухла");
        }

        String accessToken = jwtService.generateAccessToken(session.getUserId());
        SessionRefreshResponse sessionRefreshResponse = new SessionRefreshResponse(accessToken);

        String newRefreshToken = jwtService.generateRefreshToken(session.getUserId());
        session.setRefreshToken(newRefreshToken);
        session.setExpirationTime(LocalDateTime.now().plusDays(30));
        sessionRepository.save(session);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("None")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return sessionRefreshResponse;
    }
}
