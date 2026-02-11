package ru.tpu.hostel.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

    @Operation(
            summary = "Добавить кастомные контактов",
            description = "Добавляет кастомные контакты любого человека, не являющегося зарегистрированным пользователем",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное добавление контактов", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Нет прав добавлять кастомные контакты", content = @Content),
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ContactResponseDto addContact(
            @RequestPart("photoFile") MultipartFile photoFile,
            @Valid @RequestPart("contact") ContactAddRequestDto contactAddRequestDto
    ) {
        return contactService.addContact(photoFile, contactAddRequestDto);
    }

    @Operation(
            summary = "Получить все кастомные контакты",
            description = "Возвращает все контакты, которые были добавлены вручную для страницы с контактами.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение кастомных контактов", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/custom")
    public List<ContactResponseDto> getAllCustomContacts() {
        return contactService.getAllCustomContacts();
    }

    @Operation(
            summary = "Получить все контакты зарегистрированных пользователей, имеющих основные роли",
            description = "Возвращает контакты всех пользователей, у которых есть основные роли: " +
                    "ADMINISTRATION, HOSTEL_SUPERVISOR, FLOOR_SUPERVISOR, RESPONSIBLE_KITCHEN, RESPONSIBLE_HALL," +
                    " RESPONSIBLE_GYM, RESPONSIBLE_FIRE_SAFETY, RESPONSIBLE_SANITARY, RESPONSIBLE_INTERNET," +
                    " RESPONSIBLE_SOOP",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение кастомных контактов", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/users")
    public List<ContactResponseDto> getAllUsersWithRoleContacts() {
        return contactService.getAllUsersWithRoleContacts();
    }

    @DeleteMapping("/{contactId}")
    public void deleteContact(
            @Parameter(description = "ID кастомного контакта") @PathVariable UUID contactId
    ) {
        contactService.deleteContact(contactId);
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
