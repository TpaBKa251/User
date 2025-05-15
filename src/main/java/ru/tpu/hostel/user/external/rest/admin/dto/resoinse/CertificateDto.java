package ru.tpu.hostel.user.external.rest.admin.dto.resoinse;

import ru.tpu.hostel.user.external.rest.admin.dto.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record CertificateDto(
        UUID id,
        UUID user,
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {}

