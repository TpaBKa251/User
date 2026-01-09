package ru.tpu.hostel.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.user.dto.request.AddLinkRequestDto;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;
import ru.tpu.hostel.user.service.ContactService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    public ContactResponseDto addContact(@RequestBody @Valid ContactAddRequestDto contactAddRequestDto) {
        return contactService.addContact(contactAddRequestDto);
    }

    @GetMapping
    public List<ContactResponseDto> getAllContacts() {
        return contactService.getAllContacts();
    }

    @DeleteMapping()
    public void deleteContact(@RequestParam UUID id) {
        contactService.deleteContact(id);
    }

    @Operation(
            summary = "Добавить юзеру ссылку на соцсеть",
            description = "Добавляет юзеру ссылку на ВК ли ТГ (имя или ID). Если в теле не указан ID юзера, то ссылка "
                    + "добавляется текущему юзеру (отправившему запрос)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Успешное добавление ссылки"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Нет прав управлять юзером", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Кто-то изменил юзера во время выполнения запроса",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/links")
    public void addLink(@RequestBody @Valid AddLinkRequestDto addLinkRequestDto) {
        contactService.addLink(addLinkRequestDto);
    }

    @PatchMapping("/links")
    public void updateLink(@RequestBody @Valid AddLinkRequestDto addLinkRequestDto) {
        contactService.editLink(addLinkRequestDto);
    }

}
