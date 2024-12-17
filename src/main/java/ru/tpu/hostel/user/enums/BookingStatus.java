package ru.tpu.hostel.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookingStatus {
    NOT_BOOKED("Не забронировано"),
    BOOKED("Предстоящее"),
    IN_PROGRESS("В процессе"),
    CANCELLED("Отменено"),
    COMPLETED("Завершено");

    private final String bookingStatusName;
}