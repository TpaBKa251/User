package ru.tpu.hostel.user.util;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class CommonMethodsTest {

    @Test
    void getFullNameForContactWithMiddleName() {
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        String result = CommonMethods.getFullName(contact);

        assertThat(result).isEqualTo(
                TestData.LAST_NAME_IVANOV + " " + TestData.FIRST_NAME_IVAN + " " + TestData.MIDDLE_NAME);
    }

    @Test
    void getFullNameForContactWithoutMiddleName() {
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);
        contact.setMiddleName(null);

        String result = CommonMethods.getFullName(contact);

        assertThat(result).isEqualTo(TestData.LAST_NAME_IVANOV + " " + TestData.FIRST_NAME_IVAN);
    }

    @Test
    void getFullNameForUserWithMiddleName() {
        User user = TestData.defaultUser();

        String result = CommonMethods.getFullName(user);

        assertThat(result).isEqualTo(
                TestData.LAST_NAME_IVANOV + " " + TestData.FIRST_NAME_IVAN + " " + TestData.MIDDLE_NAME);
    }

    @Test
    void getFullNameForUserWithoutMiddleName() {
        User user = TestData.defaultUser();
        user.setMiddleName(null);

        String result = CommonMethods.getFullName(user);

        assertThat(result).isEqualTo(TestData.LAST_NAME_IVANOV + " " + TestData.FIRST_NAME_IVAN);
    }
}
