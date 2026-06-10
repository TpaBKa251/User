package ru.tpu.hostel.user.mapper;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.UserNameResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void mapRegisterDtoToUserWithSuccess() {
        User user = UserMapper.mapRegisterDtoToUser(TestData.userRegisterDto());

        assertThat(user.getFirstName()).isEqualTo(TestData.FIRST_NAME_IVAN);
        assertThat(user.getLastName()).isEqualTo(TestData.LAST_NAME_IVANOV);
        assertThat(user.getEmail()).isEqualTo(TestData.EMAIL_IVANOV);
        assertThat(user.getRoomNumber()).isEqualTo(TestData.ROOM_NUMBER_101);
        assertThat(user.getPassword()).isNotEqualTo(TestData.PASSWORD_RAW);
    }

    @Test
    void mapUserToUserResponseDtoWithSuccess() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        UserResponseDto result = UserMapper.mapUserToUserResponseDto(user, contact);

        assertThat(result.id()).isEqualTo(TestData.USER_ID);
        assertThat(result.tgLink()).isEqualTo(TestData.TG_LINK);
        assertThat(result.vkLink()).isEqualTo(TestData.VK_LINK);
    }

    @Test
    void mapUserToUserResponseWithNullLinksDtoWithSuccess() {
        User user = TestData.defaultUser();

        UserResponseDto result = UserMapper.mapUserToUserResponseWithNullLinksDto(user);

        assertThat(result.tgLink()).isNull();
        assertThat(result.vkLink()).isNull();
    }

    @Test
    void mapUserToUserShortResponseDtoWithSuccess() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        UserShortResponseDto result = UserMapper.mapUserToUserShortResponseDto(user, contact);

        assertThat(result.firstName()).isEqualTo(TestData.FIRST_NAME_IVAN);
        assertThat(result.tgLink()).isEqualTo(TestData.TG_LINK);
    }

    @Test
    void mapUserToUserShortResponseDto2WithSuccess() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        UserShortResponseDto2 result = UserMapper.mapUserToUserShortResponseDto2(user, contact);

        assertThat(result.id()).isEqualTo(TestData.USER_ID);
    }

    @Test
    void mapUserToUserResponseWithRoleDtoWithSuccess() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.STUDENT, user);
        user.getRoles().add(role);
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        UserResponseWithRoleDto result = UserMapper.mapUserToUserResponseWithRoleDto(user, contact);

        assertThat(result.roles()).containsExactly(Roles.STUDENT.getRoleName());
    }

    @Test
    void mapUserToUserNameResponseDtoWithSuccess() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);

        UserNameResponseDto result = UserMapper.mapUserToUserNameResponseDto(user, contact);

        assertThat(result.firstName()).isEqualTo(TestData.FIRST_NAME_IVAN);
        assertThat(result.roomNumber()).isEqualTo(TestData.ROOM_NUMBER_101);
    }
}
