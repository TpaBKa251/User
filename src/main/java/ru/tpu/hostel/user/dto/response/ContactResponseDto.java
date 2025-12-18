package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ContactResponseDto(
        @Schema(description = "ID")
        UUID id,

        String fullName,

        @Schema(description = "Название роли, которую присвоили юзеру", example = "Студент")
        String role,

        String email,

        String tgLink,

        String vkLink
) {
}
