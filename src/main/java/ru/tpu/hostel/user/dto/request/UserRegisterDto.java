package ru.tpu.hostel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Тело запроса на регистрацию юзера")
public record UserRegisterDto(

        @Schema(description = "Имя", example = "Вадим")
        @Size(message = "Имя должно быть от 1 до 50 символов", min = 1, max = 50)
        @NotBlank(message = "Имя не может быть пустым")
        @Pattern(
                regexp = "^[А-ЯЁ][а-яё]{1,50}$",
                message = "Имя должно быть написано буквами русского алфавита и начинаться с заглавной буквы"
        )
        String firstName,

        @Schema(description = "Фамилия", example = "Гудко")
        @Size(message = "Фамилия должна быть от 1 до 50 символов", min = 1, max = 50)
        @NotBlank(message = "Фамилия не может быть пустой")
        @Pattern(
                regexp = "^[А-ЯЁ][а-яё]{1,50}$",
                message = "Фамилия должна быть написана буквами русского алфавита и начинаться с заглавной буквы"
        )
        String lastName,

        @Schema(description = "Отчество (опционально)", example = "Макарыч")
        @Size(max = 50, message = "Отчество должно быть не более 50 символов")
        @Pattern(
                regexp = "^$|^([А-ЯЁ][а-яё]{0,50})$",
                message = "Отчество должно быть написано буквами русского алфавита и начинаться с заглавной буквы"
        )
        String middleName,

        @Schema(description = "Имейл")
        @Size(min = 5, max = 100, message = "Email должен быть от 5 до 100 символов")
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email. Пример: example@mail.com")
        String email,

        @Schema(description = "Номер телефона (опционально)", example = "79137235412")
        String phoneNumber,

        @Schema(description = "Пароль")
        @NotBlank(message = "Пароль не может быть пустым")
        String password,

        @Schema(description = "Номер комнаты")
        @NotBlank(message = "Номер комнаты не может быть пустым")
        @Pattern(
                regexp = "^\\d{3}[АБаб]?$",
                message = "Номер комнаты должен состоять из 3-х цифр и опционально А или Б на конце"
        )
        String roomNumber

) {
}