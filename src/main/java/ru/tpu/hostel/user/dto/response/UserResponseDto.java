package ru.tpu.hostel.user.dto.response;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phoneNumber,
        String roomNumber,
        String tgLink,
        String vkLink
) {
}
