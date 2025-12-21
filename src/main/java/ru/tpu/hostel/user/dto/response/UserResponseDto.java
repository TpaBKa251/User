package ru.tpu.hostel.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ со всей инфой о юзере, кроме пароля")
public record UserResponseDto(

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
        String roomNumber

) {
}
