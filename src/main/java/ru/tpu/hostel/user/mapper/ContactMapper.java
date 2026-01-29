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

import static ru.tpu.hostel.internal.utils.Roles.STUDENT;
import static ru.tpu.hostel.user.dto.request.LinkType.TG;

@UtilityClass
public class ContactMapper {
    public static Contact mapToContact(String fileName, ContactAddRequestDto contactAddRequestDto) {
        String[] fullName = contactAddRequestDto.fullName().split(" ");

        Contact contact = new Contact();
        contact.setLastName(fullName[0]);
        contact.setFirstName(fullName[1]);

        if (fullName.length > 2) {
            contact.setMiddleName(fullName[2]);
        }

        contact.setRole(contactAddRequestDto.role());
        contact.setEmail(contactAddRequestDto.email());

        String tgLink = contactAddRequestDto.tgLink();

        if (tgLink != null) {
            contact.setTgLink(tgLink.substring(1));
        } else {
            contact.setTgLink(null);
        }

        String vkLink = contactAddRequestDto.vkLink();

        if (contactAddRequestDto.vkLink() != null) {
            contact.setVkLink(vkLink.substring(vkLink.lastIndexOf('/') + 1));
        } else {
            contact.setVkLink(null);
        }

        contact.setCustomContact(true);
        contact.setPhotoUrl("/users/images/" + fileName);

        return contact;
    }

    public ContactResponseDto mapToContactResponseDto(Contact contact) {
        return new ContactResponseDto(
                contact.getId(),
                CommonMethods.getFullName(contact),
                contact.getRole(),
                contact.getEmail(),
                contact.getPhotoUrl(),
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

            contact.setCustomContact(false);

            contacts.add(contact);
        });

        contacts.add(getStudentContact(user, socialMediaSiteName, linkType));

        return contacts;
    }

    private Contact getStudentContact(User user, String socialMediaSiteName, LinkType linkType) {
        Contact contact = new Contact();
        contact.setFirstName(user.getFirstName());
        contact.setLastName(user.getLastName());
        contact.setMiddleName(user.getMiddleName());
        contact.setRole(STUDENT.toString());
        contact.setEmail(user.getEmail());

        if (linkType == TG) {
            contact.setTgLink(socialMediaSiteName);
        } else {
            contact.setVkLink(socialMediaSiteName);
        }

        return contact;
    }
}
