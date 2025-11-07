package ru.tpu.hostel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.internal.utils.Roles;

import java.util.UUID;

@Schema(description = "Тело запроса изменения роли юзера")
public record RoleEditDto(

        @Schema(description = "ID роли (связки юзера и роли в БД), которую нужно изменить")
        @NotNull(message = "ID роли не может быть пустым")
        UUID id,

        @Schema(description = "Имя енама роли, на которую происходит изменение")
        @NotNull(message = "Роль не может быть пустой")
        Roles role

) {
}
