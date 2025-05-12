package ru.tpu.hostel.user.dto.response;

import java.util.UUID;

public record RoleResponseDto(
        UUID id,
        UUID user,
        String role
) {
}
