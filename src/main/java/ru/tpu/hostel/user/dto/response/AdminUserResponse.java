package ru.tpu.hostel.user.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String room,
        CertificateDto pediculosis,
        CertificateDto fluorography,
        BigDecimal balance
) {
}
