package ru.tpu.hostel.user.dto.response;

import ru.tpu.hostel.user.enums.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record CertificateDto(
        UUID id,
        UUID user,
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {}

