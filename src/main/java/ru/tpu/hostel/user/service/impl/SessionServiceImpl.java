package ru.tpu.hostel.user.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.internal.common.logging.LogFilter;
import ru.tpu.hostel.internal.common.logging.SecretArgument;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.user.dto.request.SessionLoginDto;
import ru.tpu.hostel.user.dto.response.SessionRefreshResponse;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;
import ru.tpu.hostel.user.entity.Session;
import ru.tpu.hostel.user.entity.User;
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

    private static final String SESSION_NOT_FOUND_EXCEPTION_MESSAGE = "Сессия не найдена";

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final SessionRepository sessionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Transactional
    @LogFilter(enableParamsLogging = false, enableResultLogging = false)
    @Override
    public SessionResponseDto login(
            @SecretArgument SessionLoginDto sessionLoginDto,
            @SecretArgument HttpServletResponse response
    ) {
        User user = userRepository.findByEmail(sessionLoginDto.email())
                .filter(user1 -> passwordEncoder.matches(sessionLoginDto.password(), user1.getPassword()))
                .orElseThrow(() -> new ServiceException.Unauthorized("Неверные логин или пароль"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Session session = new Session();
        session.setUserId(user);
        session.setCreateTime(LocalDateTime.now());
        session.setExpirationTime(LocalDateTime.now().plusDays(30));
        session.setRefreshToken(refreshToken);
        sessionRepository.save(session);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new SessionResponseDto(session.getId(), accessToken);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    @LogFilter(enableParamsLogging = false)
    @Override
    public void logout(@SecretArgument UUID sessionId, @SecretArgument HttpServletResponse response) {
        Session session = sessionRepository.findByIdOptimistic(sessionId)
                .orElseThrow(() -> new ServiceException.NotFound(SESSION_NOT_FOUND_EXCEPTION_MESSAGE));

        if (!session.getUserId().getId().equals(ExecutionContext.get().getUserID())) {
            throw new ServiceException.Forbidden("Вы не можете выйти из чужой сессии");
        }

        session.setRefreshToken(null);
        sessionRepository.save(session);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @LogFilter(enableParamsLogging = false, enableResultLogging = false)
    @Override
    public SessionRefreshResponse refresh(
            @SecretArgument String refreshToken,
            @SecretArgument HttpServletResponse response
    ) {
        jwtService.checkRefreshTokenValidity(refreshToken);
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ServiceException.NotFound(SESSION_NOT_FOUND_EXCEPTION_MESSAGE));

        if (session.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException.Forbidden("Сессия уже потухла");
        }

        String accessToken = jwtService.generateAccessToken(session.getUserId());
        SessionRefreshResponse sessionRefreshResponse = new SessionRefreshResponse(accessToken);

        String newRefreshToken = jwtService.generateRefreshToken(session.getUserId());
        session.setRefreshToken(newRefreshToken);
        session.setExpirationTime(LocalDateTime.now().plusDays(30));
        sessionRepository.save(session);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, newRefreshToken)
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
