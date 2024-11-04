package ru.tpu.hostel.user.dto.response;

import java.util.UUID;

public record SessionResponseDto(
        UUID id,
        String accessToken
) {
}
