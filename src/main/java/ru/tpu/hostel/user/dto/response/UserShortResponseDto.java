package ru.tpu.hostel.user.dto.response;

public record UserShortResponseDto(
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
