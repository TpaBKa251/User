package ru.tpu.hostel.user.mapper;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

    @Test
    void mapRoleToRoleResponseDtoWithSuccess() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.STUDENT, user);

        RoleResponseDto result = RoleMapper.mapRoleToRoleResponseDto(role);

        assertThat(result.id()).isEqualTo(TestData.ROLE_ID);
        assertThat(result.user()).isEqualTo(TestData.USER_ID);
        assertThat(result.role()).isEqualTo(Roles.STUDENT.getRoleName());
    }
}
