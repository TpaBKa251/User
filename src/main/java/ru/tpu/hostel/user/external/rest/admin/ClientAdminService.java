package ru.tpu.hostel.user.external.rest.admin;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tpu.hostel.user.external.rest.admin.dto.request.BalanceRequestDto;
import ru.tpu.hostel.user.external.rest.admin.dto.request.DocumentRequestDto;
import ru.tpu.hostel.user.external.rest.admin.dto.resoinse.CertificateDto;
import ru.tpu.hostel.user.external.rest.admin.dto.DocumentType;

import java.util.UUID;


@Component
@FeignClient(name = "administration-administrationservice", url = "${rest.base-url.admin-service}")
public interface ClientAdminService {

    @GetMapping("balance/get/short/{id}")
    ResponseEntity<?> getBalanceShort(@PathVariable UUID id);

    @GetMapping("documents/get/type/{documentType}/user/{userId}")
    CertificateDto getDocumentByType(@PathVariable UUID userId, @PathVariable DocumentType documentType);

    @PostMapping("/balance")
    ResponseEntity<?> addBalance(@RequestBody @Valid BalanceRequestDto balanceRequestDto);

    @PostMapping("/documents")
    ResponseEntity<?> addDocument(@RequestBody @Valid DocumentRequestDto documentRequestDto);
}
