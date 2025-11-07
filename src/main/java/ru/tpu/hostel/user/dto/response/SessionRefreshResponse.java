package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с обновленным access токеном")
public record SessionRefreshResponse(

        @Schema(description = "Access токен")
        String token

) {
}
