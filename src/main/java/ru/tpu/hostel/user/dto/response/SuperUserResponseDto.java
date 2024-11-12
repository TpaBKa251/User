package ru.tpu.hostel.user.dto.response;

import org.springframework.http.ResponseEntity;
import ru.tpu.hostel.user.enums.Roles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SuperUserResponseDto(
        UUID id,
        String firstName,
        String lastName,
        String middleName,
        String roomNumber,
        List<String> role,
        BigDecimal balance,
        CertificateDto certificateFluorography,
        CertificateDto certificatePediculosis,
        List<ActiveEventDto> activeEvents
) {}

