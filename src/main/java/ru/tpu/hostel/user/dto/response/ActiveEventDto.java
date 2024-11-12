package ru.tpu.hostel.user.dto.response;

import ru.tpu.hostel.user.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActiveEventDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status,
        String type
) {}