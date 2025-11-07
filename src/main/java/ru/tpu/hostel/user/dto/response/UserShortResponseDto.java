package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ со всей инфой о юзере, кроме пароля и ID")
public record UserShortResponseDto(

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

        @Schema(description = "Имя в ТГ")
        String tgLink,

        @Schema(description = "Имя в ВК")
        String vkLink

) {
}
