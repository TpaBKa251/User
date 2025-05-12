package ru.tpu.hostel.user.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.internal.utils.Roles;

import java.util.UUID;

public record RoleEditDto(
        @NotNull
        UUID id,

        @NotNull
        Roles role
) {
}
