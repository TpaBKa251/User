package ru.tpu.hostel.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserAddLinkDto(@NotEmpty @NotNull String link) {
}
