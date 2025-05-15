package ru.tpu.hostel.user.external.rest.admin.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentRequestDto(
        @NotNull(message = "Пользователь не может быть пустым")
        UUID user,

        @NotNull(message = "Тип документа не может быть пустым")
        String type,

        @NotNull(message = "Стартовая дата не может быть пустой")
        LocalDate startDate,

        @NotNull(message = "Конечная дата не может быть пустой")
        LocalDate endDate
) {
}
