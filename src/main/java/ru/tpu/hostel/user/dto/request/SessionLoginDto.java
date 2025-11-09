package ru.tpu.hostel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Тело запроса для входа в аккаунт")
public record SessionLoginDto(

        @Schema(description = "Имейл")
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Email в неверном формате. Пример: example@mail.com")
        String email,

        @Schema(description = "Пароль")
        @NotBlank(message = "Пароль не может быть пустым")
        String password

) {
}