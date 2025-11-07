package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Ответ со всей инфой о юзере вместе с ролями")
public record UserResponseWithRoleDto(

        @Schema(description = "ID")
        UUID id,

        @Schema(description = "Имя")
        String firstName,

        @Schema(description = "Фамилия")
        String lastName,

        @Schema(description = "Отчество")
        String middleName,

        @Schema(description = "Имейл")
        String email,

        @Schema(description = "Номер телефона")
        String phoneNumber,

        @Schema(description = "Номер комнаты")
        String roomNumber,

        @Schema(description = "Список ролей")
        List<String> roles,

        @Schema(description = "Имя в ТГ")
        String tgLink,

        @Schema(description = "Имя в ВК")
        String vkLink

) {
}
