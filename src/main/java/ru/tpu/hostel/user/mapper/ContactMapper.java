package ru.tpu.hostel.user.mapper;

import lombok.experimental.UtilityClass;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.request.LinkType;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.util.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import static ru.tpu.hostel.user.dto.request.LinkType.TG;

@UtilityClass
public class ContactMapper {
    public static Contact mapToContact(ContactAddRequestDto contactAddRequestDto) {
        String[] fullName = contactAddRequestDto.fullName().split(" ");

        Contact contact = new Contact();
        contact.setLastName(fullName[0]);
        contact.setFirstName(fullName[1]);

        if (fullName.length > 2) {
            contact.setMiddleName(fullName[2]);
        }

        contact.setRole(contactAddRequestDto.role());
        contact.setEmail(contactAddRequestDto.email());
        contact.setTgLink(contactAddRequestDto.tgLink());
        contact.setVkLink(contactAddRequestDto.vkLink());

        return contact;
    }

    public ContactResponseDto mapToContactResponseDto(Contact contact) {
        return new ContactResponseDto(
                contact.getId(),
                CommonMethods.getFullName(contact),
                contact.getRole(),
                contact.getEmail(),
                contact.getTgLink(),
                contact.getVkLink()
        );
    }

    public List<Contact> createContacts(
            User user,
            List<Roles> roles,
            String socialMediaSiteName,
            LinkType linkType
    ) {
        List<Contact> contacts = new ArrayList<>();

        roles.forEach(role -> {
            Contact contact = new Contact();
            contact.setFirstName(user.getFirstName());
            contact.setLastName(user.getLastName());
            contact.setMiddleName(user.getMiddleName());
            contact.setRole(role.toString());
            contact.setEmail(user.getEmail());

            if (linkType == TG) {
                contact.setTgLink(socialMediaSiteName);
            } else {
                contact.setVkLink(socialMediaSiteName);
            }

            contacts.add(contact);
        });

        return contacts;
    }
}
