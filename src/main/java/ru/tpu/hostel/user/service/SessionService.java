package ru.tpu.hostel.user.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import ru.tpu.hostel.user.dto.request.SessionLoginDto;
import ru.tpu.hostel.user.dto.response.SessionRefreshResponse;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;

import java.util.UUID;

public interface SessionService {

    SessionResponseDto login(SessionLoginDto sessionLoginDto, HttpServletResponse response);

    ResponseEntity<?> logout(UUID sessionId, UUID userId, HttpServletResponse response);

    SessionRefreshResponse refresh(String refreshToken, HttpServletResponse response);
}
