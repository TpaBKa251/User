package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ с информацией о роли (связки юзера и роли в БД)")
public record RoleResponseDto(

        @Schema(description = "ID роли (связки юзера и роли в БД)")
        UUID id,

        @Schema(description = "ID юзера, которому присвоили роль")
        UUID user,

        @Schema(description = "Название роли, которую присвоили юзеру", example = "Студент")
        String role

) {
}
