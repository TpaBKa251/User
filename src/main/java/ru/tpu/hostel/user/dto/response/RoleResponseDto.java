package ru.tpu.hostel.user.dto.response;

import ru.tpu.hostel.user.enums.Roles;

import java.util.UUID;

public record RoleResponseDto(
        UUID id,
        UUID user,
        String role
) {
}
