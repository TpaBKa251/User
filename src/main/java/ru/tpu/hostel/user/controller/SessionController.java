package ru.tpu.hostel.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.user.dto.request.SessionLoginDto;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;
import ru.tpu.hostel.user.service.SessionService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public SessionResponseDto login(@RequestBody @Valid SessionLoginDto sessionLoginDto, HttpServletResponse response) {
        return sessionService.login(sessionLoginDto, response);
    }

    @PatchMapping("/logout/{sessionId}/{userId}")
    public ResponseEntity<?> logout(@PathVariable UUID sessionId, @PathVariable UUID userId, HttpServletResponse response) {
        return sessionService.logout(sessionId, userId, response);
    }
}
