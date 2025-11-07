package ru.tpu.hostel.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.internal.utils.Roles;

import java.util.UUID;

@Schema(description = "Тело запроса присваивания роли юзеру")
public record RoleSetDto(

        @Schema(description = "ID юзера, которому присваивается роль")
        @NotNull(message = "ID пользователя не может быть пустым")
        UUID user,

        @Schema(description = "Присваиваемая роль в виде имени енама")
        @NotNull(message = "Роль не может быть пустой")
        Roles role

) {
}
