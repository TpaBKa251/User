package ru.tpu.hostel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Тело запроса добавления/изменения контактов (ссылок на соцсети) юзера")
public record UserAddLinkDto(

        @Schema(
                description = "Имя в ТГ или ВК либо ссылка на профиль ТГ или ВК. "
                        + "Допустимые форматы: "
                        + "1) @username - юзернейм с собакой, "
                        + "2) username - юзернейм без собаки, "
                        + "3) https://t.me/username - полная ссылка, "
                        + "4) t.me/username - короткая ссылка",
                example = "@kik_butovski"
        )
        @NotBlank(message = "Ссылка или имя пользователя не может быть пустым")
        String link,

        @Schema(
                description = "ID юзера (опционально). "
                        + "Если null, то ссылка добавляется текущему юзеру (отправившему запрос)"
        )
        UUID userId

) {
}
