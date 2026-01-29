package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ContactResponseDto(
        @Schema(description = "ID")
        UUID id,

        @Schema(description = "ФИО человека")
        String fullName,

        @Schema(description = "Название роли, которую присвоили юзеру", example = "Студент")
        String role,

        @Schema(description = "Имейл")
        String email,

        @Schema(description = "Ссылка на фото")
        String photoUrl,

        @Schema(description = "имя в ТГ")
        String tgLink,

        @Schema(description = "ссылка на ВК")
        String vkLink
) {
}
