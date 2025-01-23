package ru.tpu.hostel.user.dto.response;

public record UserNameResponseDto(
        String firstName,
        String lastName,
        String middleName,
        String roomNumber
) {
}
