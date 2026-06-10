package ru.tpu.hostel.user.mapper;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.request.LinkType;
import ru.tpu.hostel.user.dto.response.ContactResponseDto;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContactMapperTest {

    private static final String FILE_NAME = "photo.png";

    @Test
    void mapToContactWithSuccess() {
        Contact contact = ContactMapper.mapToContact(FILE_NAME, TestData.contactAddRequestDto());

        assertThat(contact.getLastName()).isEqualTo(TestData.LAST_NAME_IVANOV);
        assertThat(contact.getFirstName()).isEqualTo(TestData.FIRST_NAME_IVAN);
        assertThat(contact.getMiddleName()).isEqualTo(TestData.MIDDLE_NAME);
        assertThat(contact.getTgLink()).isEqualTo(TestData.TG_LINK);
        assertThat(contact.getVkLink()).isEqualTo(TestData.VK_LINK);
        assertThat(contact.getCustomContact()).isTrue();
        assertThat(contact.getPhotoUrl()).isEqualTo("/users/images/" + FILE_NAME);
    }

    @Test
    void mapToContactWhenLinksAreNull() {
        ContactAddRequestDto dto = new ContactAddRequestDto(
                TestData.LAST_NAME_IVANOV + " " + TestData.FIRST_NAME_IVAN,
                Roles.STUDENT.toString(),
                TestData.EMAIL_IVANOV,
                null,
                null
        );

        Contact contact = ContactMapper.mapToContact(FILE_NAME, dto);

        assertThat(contact.getTgLink()).isNull();
        assertThat(contact.getVkLink()).isNull();
        assertThat(contact.getMiddleName()).isNull();
    }

    @Test
    void mapToContactResponseDtoWithSuccess() {
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        ContactResponseDto result = ContactMapper.mapToContactResponseDto(contact);

        assertThat(result.id()).isEqualTo(TestData.CONTACT_ID);
        assertThat(result.email()).isEqualTo(TestData.EMAIL_IVANOV);
    }

    @Test
    void createContactsWithTgLink() {
        User user = TestData.defaultUser();

        List<Contact> contacts = ContactMapper.createContacts(
                user, List.of(Roles.HOSTEL_SUPERVISOR), TestData.TG_LINK, LinkType.TG);

        assertThat(contacts).hasSize(2);
        assertThat(contacts).allSatisfy(contact -> assertThat(contact.getTgLink()).isEqualTo(TestData.TG_LINK));
    }

    @Test
    void createContactsWithVkLink() {
        User user = TestData.defaultUser();

        List<Contact> contacts = ContactMapper.createContacts(
                user, List.of(Roles.HOSTEL_SUPERVISOR), TestData.VK_LINK, LinkType.VK);

        assertThat(contacts).hasSize(2);
        assertThat(contacts).allSatisfy(contact -> assertThat(contact.getVkLink()).isEqualTo(TestData.VK_LINK));
    }
}
