package ru.tpu.hostel.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationRequestDto;
import ru.tpu.hostel.internal.service.NotificationSender;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.impl.RoleServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.tpu.hostel.internal.exception.ServiceException.Conflict;
import static ru.tpu.hostel.internal.exception.ServiceException.Forbidden;
import static ru.tpu.hostel.internal.exception.ServiceException.NotFound;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void setRoleWithSuccess() {
        User user = TestData.newUser(
                TestData.OTHER_USER_ID, TestData.FIRST_NAME_BOGDAN, TestData.LAST_NAME_BOGDANOV,
                TestData.EMAIL_BOGDANOV, TestData.ROOM_NUMBER_102);
        when(userRepository.findById(TestData.OTHER_USER_ID)).thenReturn(Optional.of(user));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            RoleResponseDto result =
                    roleService.setRole(TestData.roleSetDto(TestData.OTHER_USER_ID, Roles.HOSTEL_SUPERVISOR));

            assertThat(result.user()).isEqualTo(TestData.OTHER_USER_ID);
            verify(roleRepository).save(any(Role.class));
            verify(notificationSender).sendNotification(any(NotificationRequestDto.class));
        }
    }

    @Test
    void setRoleWhenNoPermission() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.STUDENT));

            assertThatThrownBy(() ->
                    roleService.setRole(TestData.roleSetDto(TestData.OTHER_USER_ID, Roles.ADMINISTRATION)))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void setRoleWhenUserNotFound() {
        when(userRepository.findById(TestData.OTHER_USER_ID)).thenReturn(Optional.empty());

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() ->
                    roleService.setRole(TestData.roleSetDto(TestData.OTHER_USER_ID, Roles.HOSTEL_SUPERVISOR)))
                    .isInstanceOf(NotFound.class);
        }
    }

    @Test
    void setRoleWhenUserAlreadyHasRole() {
        User user = TestData.newUser(
                TestData.OTHER_USER_ID, TestData.FIRST_NAME_BOGDAN, TestData.LAST_NAME_BOGDANOV,
                TestData.EMAIL_BOGDANOV, TestData.ROOM_NUMBER_102);
        when(userRepository.findById(TestData.OTHER_USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.save(any(Role.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() ->
                    roleService.setRole(TestData.roleSetDto(TestData.OTHER_USER_ID, Roles.HOSTEL_SUPERVISOR)))
                    .isInstanceOf(Conflict.class);
        }
    }

    @Test
    void setRoleWhenTransferringRole() {
        User currentUser = TestData.defaultUser();
        Role currentRole = TestData.newRole(TestData.ROLE_ID, Roles.FLOOR_SUPERVISOR, currentUser);
        currentUser.getRoles().add(currentRole);
        User targetUser = TestData.newUser(
                TestData.OTHER_USER_ID, TestData.FIRST_NAME_BOGDAN, TestData.LAST_NAME_BOGDANOV,
                TestData.EMAIL_BOGDANOV, TestData.ROOM_NUMBER_102);

        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(TestData.OTHER_USER_ID)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findByUser(currentUser)).thenReturn(List.of(currentRole));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.FLOOR_SUPERVISOR));
            when(context.getUserID()).thenReturn(TestData.USER_ID);

            RoleResponseDto result =
                    roleService.setRole(TestData.roleSetDto(TestData.OTHER_USER_ID, Roles.FLOOR_SUPERVISOR));

            assertThat(result.user()).isEqualTo(TestData.OTHER_USER_ID);
            verify(roleRepository).deleteById(TestData.ROLE_ID);
        }
    }

    @Test
    void editRoleWithSuccess() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.HOSTEL_SUPERVISOR, user);
        when(roleRepository.findByIdOptimistic(TestData.ROLE_ID)).thenReturn(Optional.of(role));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            RoleResponseDto result = roleService.editRole(TestData.roleEditDto(TestData.ROLE_ID, Roles.FLOOR_SUPERVISOR));

            assertThat(result.id()).isEqualTo(TestData.ROLE_ID);
            verify(notificationSender).sendNotification(any(NotificationRequestDto.class));
        }
    }

    @Test
    void editRoleWhenNoPermission() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.STUDENT));

            assertThatThrownBy(() ->
                    roleService.editRole(TestData.roleEditDto(TestData.ROLE_ID, Roles.ADMINISTRATION)))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void editRoleWhenRoleNotFound() {
        when(roleRepository.findByIdOptimistic(TestData.ROLE_ID)).thenReturn(Optional.empty());

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() ->
                    roleService.editRole(TestData.roleEditDto(TestData.ROLE_ID, Roles.FLOOR_SUPERVISOR)))
                    .isInstanceOf(NotFound.class);
        }
    }

    @Test
    void editRoleWhenConflict() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.HOSTEL_SUPERVISOR, user);
        when(roleRepository.findByIdOptimistic(TestData.ROLE_ID)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenThrow(new DataIntegrityViolationException("duplicate"));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() ->
                    roleService.editRole(TestData.roleEditDto(TestData.ROLE_ID, Roles.FLOOR_SUPERVISOR)))
                    .isInstanceOf(Conflict.class);
        }
    }

    @Test
    void editRoleWhenOptimisticLockFailure() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.HOSTEL_SUPERVISOR, user);
        when(roleRepository.findByIdOptimistic(TestData.ROLE_ID)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Role.class, TestData.ROLE_ID));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() ->
                    roleService.editRole(TestData.roleEditDto(TestData.ROLE_ID, Roles.FLOOR_SUPERVISOR)))
                    .isInstanceOf(Conflict.class);
        }
    }

    @Test
    void getRoleWhenFound() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.STUDENT, user);
        when(roleRepository.findById(TestData.ROLE_ID)).thenReturn(Optional.of(role));

        RoleResponseDto result = roleService.getRole(TestData.ROLE_ID);

        assertThat(result.id()).isEqualTo(TestData.ROLE_ID);
    }

    @Test
    void getRoleWhenNotFound() {
        when(roleRepository.findById(TestData.ROLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRole(TestData.ROLE_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getUserRolesWhenFound() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.STUDENT, user);
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByUser(user)).thenReturn(List.of(role));

        List<RoleResponseDto> result = roleService.getUserRoles(TestData.USER_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUserRolesWhenUserNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getUserRoles(TestData.USER_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getAllUserRolesWhenFound() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.ADMINISTRATION, user);
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByUser(user)).thenReturn(List.of(role));

        List<String> result = roleService.getAllUserRoles(TestData.USER_ID);

        assertThat(result).contains(Roles.ADMINISTRATION.toString());
    }

    @Test
    void getAllUserRolesWhenUserNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getAllUserRoles(TestData.USER_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getUsersWithRoleWithSuccess() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.STUDENT, user);
        when(roleRepository.findByRole(Roles.STUDENT)).thenReturn(List.of(role));

        List<RoleResponseDto> result = roleService.getUsersWithRole(Roles.STUDENT);

        assertThat(result).hasSize(1);
    }

    @Test
    void deleteRoleWithSuccess() {
        User user = TestData.defaultUser();
        Role role = TestData.newRole(TestData.ROLE_ID, Roles.HOSTEL_SUPERVISOR, user);
        user.getRoles().add(role);
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            roleService.deleteRole(TestData.USER_ID, Roles.HOSTEL_SUPERVISOR);

            verify(roleRepository).deleteByUserAndRole(user, Roles.HOSTEL_SUPERVISOR);
            verify(notificationSender).sendNotification(any(NotificationRequestDto.class));
        }
    }

    @Test
    void deleteRoleWhenNoPermission() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.STUDENT));

            assertThatThrownBy(() -> roleService.deleteRole(TestData.USER_ID, Roles.ADMINISTRATION))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void deleteRoleWhenUserNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() -> roleService.deleteRole(TestData.USER_ID, Roles.HOSTEL_SUPERVISOR))
                    .isInstanceOf(NotFound.class);
        }
    }

    @Test
    void deleteRoleWhenUserDoesNotHaveRole() {
        User user = TestData.defaultUser();
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserRoles()).thenReturn(Set.of(Roles.ADMINISTRATION));

            assertThatThrownBy(() -> roleService.deleteRole(TestData.USER_ID, Roles.HOSTEL_SUPERVISOR))
                    .isInstanceOf(Conflict.class);
        }
    }
}
