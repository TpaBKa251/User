package ru.tpu.hostel.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.user.dto.request.SessionLoginDto;
import ru.tpu.hostel.user.dto.response.SessionRefreshResponse;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;
import ru.tpu.hostel.user.service.SessionService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public SessionResponseDto login(
            @RequestBody @Valid SessionLoginDto sessionLoginDto,
            HttpServletResponse response
    ) {
        return sessionService.login(sessionLoginDto, response);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/logout/{sessionId}")
    public void logout(
            @PathVariable UUID sessionId,
            HttpServletResponse response
    ) {
        sessionService.logout(sessionId, response);
    }

    @GetMapping("/auth/token")
    public SessionRefreshResponse refreshToken(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return sessionService.refresh(refreshToken, response);
    }

    @PostMapping("/auth/token")
    public SessionRefreshResponse refreshTokenPost(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return sessionService.refresh(refreshToken, response);
    }
}
