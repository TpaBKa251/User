package ru.tpu.hostel.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.tpu.hostel.user.dto.response.CertificateDto;
import ru.tpu.hostel.user.enums.DocumentType;

import java.util.UUID;

@Component
@FeignClient(name = "administration-administrationservice", url = "http://administrationservice:8080")
public interface ClientAdminService {

    @GetMapping("balance/get/short/{id}")
    ResponseEntity<?> getBalanceShort(@PathVariable UUID id);

    @GetMapping("documents/get/type/{documentType}/user/{userId}")
    CertificateDto getDocumentByType(@PathVariable UUID userId, @PathVariable DocumentType documentType);
}
