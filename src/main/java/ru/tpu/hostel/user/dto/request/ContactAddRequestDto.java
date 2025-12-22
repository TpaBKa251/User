package ru.tpu.hostel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactAddRequestDto(
        @Schema(description = "ФИО человека")
        @NotNull(message = "ФИО не может быть пустым")
        String fullName,

        @Schema(description = "Присваиваемая роль в виде имени енама")
        @NotNull(message = "Роль не может быть пустой")
        String role,

        @Schema(description = "Имейл")
        @Size(min = 5, max = 100, message = "Email должен быть от 5 до 100 символов")
        @NotNull(message = "Email не может быть пустым")
        @Email(message = "Неверный формат email. Пример: example@mail.com")
        String email,

        @Schema(
                description = "Имя в ТГ или ссылка на профиль ТГ. "
                        + "Допустимые форматы: "
                        + "1) @username - юзернейм с собакой, "
                        + "2) username - юзернейм без собаки, "
                        + "3) https://t.me/username - полная ссылка, "
                        + "4) t.me/username - короткая ссылка",
                example = "@kik_butovski"
        )
        String tgLink,

        @Schema(
                description = "ссылка на профиль ВК. "
                        + "Допустимые форматы: "
                        + "1) https://t.me/username - полная ссылка, ",
                example = "@kik_butovski"
        )
        String vkLink
) {
}
