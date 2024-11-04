package ru.tpu.hostel.user.dto.request;

import java.util.UUID;

public record SessionRefreshDto(
        UUID userId
) {
}
