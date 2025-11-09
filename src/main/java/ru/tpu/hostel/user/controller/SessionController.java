package ru.tpu.hostel.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Сессии", description = "Эндпоинты для работы с сессиями")
@ApiResponse(responseCode = "500", description = "Неизвестная ошибка сервера", content = @Content)
@ApiResponse(
        responseCode = "400",
        description = "Неверный запрос от клиента, нарушение ограничений запроса (тело, параметры)",
        content = @Content
)
public class SessionController {

    private final SessionService sessionService;

    @Operation(
            summary = "Войти в аккаунт",
            description = "Создает новую сессию юзера",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный вход в аккаунт"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Указан неверный логин или пароль",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public SessionResponseDto login(
            @RequestBody @Valid SessionLoginDto sessionLoginDto,
            HttpServletResponse response
    ) {
        return sessionService.login(sessionLoginDto, response);
    }

    @Operation(
            summary = "Выйти из аккаунта",
            description = "Помечает сессию неактивной, удаляет refresh токен",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Успешный выход из аккаунта"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Запрос не авторизован",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Попытка выхода из чужой сессии",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "404", description = "Сессия не найдена", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/logout/{sessionId}")
    public void logout(
            @Parameter(description = "ID сессии") @PathVariable UUID sessionId,
            HttpServletResponse response
    ) {
        sessionService.logout(sessionId, response);
    }

    @Operation(
            summary = "Обновить access токен",
            description = "Обновляет access и refresh токены для сессии по refresh токену",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное обновление токенов"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Запрос не авторизован (refresh токен протух или неверный)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Сессия уже протухла",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "404", description = "Сессия не найдена", content = @Content)
            }
    )
    @GetMapping("/auth/token")
    public SessionRefreshResponse refreshToken(
            @Parameter(description = "Refresh токен") @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return sessionService.refresh(refreshToken, response);
    }

    @Operation(
            summary = "Обновить access токен",
            description = "Обновляет access и refresh токены для сессии по refresh токену",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное обновление токенов"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Запрос не авторизован (refresh токен протух или неверный)",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Сессия уже протухла",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "404", description = "Сессия не найдена", content = @Content)
            }
    )
    @PostMapping("/auth/token")
    public SessionRefreshResponse refreshTokenPost(
            @Parameter(description = "Refresh токен") @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return sessionService.refresh(refreshToken, response);
    }

}