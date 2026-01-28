package ru.tpu.hostel.user.service;

import org.springframework.web.multipart.MultipartFile;
import ru.tpu.hostel.user.dto.request.AddLinkRequestDto;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    ContactResponseDto addContact(MultipartFile photoFile, ContactAddRequestDto contactAddRequestDto);
    List<ContactResponseDto> getAllCustomContacts();
    List<ContactResponseDto> getAllUsersWithRoleContacts();
    void deleteContact(UUID id);
    void addLink(AddLinkRequestDto addLinkRequestDto);
    void editLink(AddLinkRequestDto addLinkRequestDto);
}
