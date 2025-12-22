package ru.tpu.hostel.user.service;

import ru.tpu.hostel.user.dto.request.AddLinkRequestDto;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    ContactResponseDto addContact(ContactAddRequestDto contactAddRequestDto);
    List<ContactResponseDto> getAllContacts();
    void deleteContact(UUID id);
    void addLink(AddLinkRequestDto addLinkRequestDto);
}
