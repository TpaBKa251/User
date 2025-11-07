package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ с инфой о сессии")
public record SessionResponseDto(

        @Schema(description = "ID сессии")
        UUID id,

        @Schema(description = "Access токен")
        String accessToken

) {
}
