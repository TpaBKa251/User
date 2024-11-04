package ru.tpu.hostel.user.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponseWithRoleDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phoneNumber,
        String roomNumber,
        List<String> roles
) {
}
