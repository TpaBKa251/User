package ru.tpu.hostel.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.repository.util.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    private Contact custom;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();

        custom = new Contact();
        custom.setFirstName(TestData.FIRST_NAME_IVAN);
        custom.setLastName(TestData.LAST_NAME_IVANOV);
        custom.setRole(Roles.ADMINISTRATION.toString());
        custom.setEmail(TestData.EMAIL_IVANOV);
        custom.setCustomContact(true);
        custom = contactRepository.save(custom);

        Contact main = new Contact();
        main.setFirstName(TestData.FIRST_NAME_BOGDAN);
        main.setLastName(TestData.LAST_NAME_BOGDANOV);
        main.setRole(Roles.HOSTEL_SUPERVISOR.toString());
        main.setEmail(TestData.EMAIL_BOGDANOV);
        main.setCustomContact(false);
        contactRepository.save(main);
    }

    @Test
    void getAllCustomContactsWithSuccess() {
        List<Contact> result = contactRepository.getAllCustomContacts();

        assertThat(result).extracting(Contact::getEmail).containsExactly(TestData.EMAIL_IVANOV);
    }

    @Test
    void getAllMainContactsWithSuccess() {
        List<Contact> result = contactRepository.getAllMainContacts(List.of(Roles.HOSTEL_SUPERVISOR.toString()));

        assertThat(result).extracting(Contact::getEmail).containsExactly(TestData.EMAIL_BOGDANOV);
    }

    @Test
    void findFirstByEmailWithSuccess() {
        Optional<Contact> result = contactRepository.findFirstByEmail(TestData.EMAIL_IVANOV);

        assertThat(result).isPresent();
    }

    @Test
    void findAllByEmailWithSuccess() {
        List<Contact> result = contactRepository.findAllByEmail(TestData.EMAIL_IVANOV);

        assertThat(result).hasSize(1);
    }

    @Test
    void updateTgLinkWithSuccess() {
        contactRepository.updateTgLink(custom.getId(), TestData.TG_LINK);
        contactRepository.flush();

        Optional<Contact> result = contactRepository.findById(custom.getId());
        assertThat(result).isPresent();
    }
}
