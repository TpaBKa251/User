package ru.tpu.hostel.user.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.user.enums.Roles;

import java.util.UUID;

public record RoleEditDto(
        @NotNull
        UUID id,

        @NotNull
        Roles role
) {
}
