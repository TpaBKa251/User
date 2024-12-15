package ru.tpu.hostel.user.dto.response;

/**
 * @deprecated заменено на {@link UserShortResponseDto2}
 */
@Deprecated
public record UserShortResponseDto(
        String firstName,
        String lastName,
        String middleName,
        String email,
        String phoneNumber,
        String roomNumber
) {
}
