package ru.tpu.hostel.user.external.rest.admin;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.tpu.hostel.user.external.rest.admin.dto.request.BalanceRequestDto;
import ru.tpu.hostel.user.external.rest.admin.dto.request.DocumentRequestDto;


@Component
@FeignClient(name = "administration-administrationservice", url = "${rest.base-url.admin-service}")
public interface ClientAdminService {

    @PostMapping("/balance")
    ResponseEntity<?> addBalance(@RequestBody @Valid BalanceRequestDto balanceRequestDto);

    @PostMapping("/documents")
    ResponseEntity<?> addDocument(@RequestBody @Valid DocumentRequestDto documentRequestDto);
}
