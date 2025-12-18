package ru.tpu.hostel.user.dto.request;

public record ContactAddRequestDto(
        String fullName,
        String role,
        String email,
        String tgLink,
        String vkLink
) {
}
