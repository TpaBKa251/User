package ru.tpu.hostel.user.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceRequestDto(
        @NotNull(message = "Пользователь не может быть пустым")
        UUID user,

        @NotNull(message = "Баланс не может быть пустым")
        BigDecimal balance
) {
}
