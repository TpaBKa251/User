package ru.tpu.hostel.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.UserNameResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.external.rest.admin.ClientAdminService;
import ru.tpu.hostel.user.repository.ContactRepository;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.tpu.hostel.internal.exception.ServiceException.NotFound;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private ClientAdminService adminService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUserWithSuccess() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(TestData.USER_ID);
            return user;
        });

        UserResponseDto result = userService.registerUser(TestData.userRegisterDto());

        assertThat(result.id()).isEqualTo(TestData.USER_ID);
        assertThat(result.firstName()).isEqualTo(TestData.FIRST_NAME_IVAN);
        assertThat(result.tgLink()).isNull();
        verify(userRepository).save(any(User.class));
        verify(roleRepository).save(any());
        verify(adminService).addBalance(any());
        verify(adminService, times(2)).addDocument(any());
    }

    @Test
    void getUserWhenFound() {
        User user = TestData.defaultUser();
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserID()).thenReturn(TestData.USER_ID);
            when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
            when(contactRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.empty());

            UserResponseDto result = userService.getUser();

            assertThat(result.id()).isEqualTo(TestData.USER_ID);
            assertThat(result.email()).isEqualTo(TestData.EMAIL_IVANOV);
        }
    }

    @Test
    void getUserWhenNotFound() {
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserID()).thenReturn(TestData.USER_ID);
            when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUser()).isInstanceOf(NotFound.class);
        }
    }

    @Test
    void getUserByIdWhenFound() {
        User user = TestData.defaultUser();
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        UserShortResponseDto result = userService.getUserById(TestData.USER_ID);

        assertThat(result.firstName()).isEqualTo(TestData.FIRST_NAME_IVAN);
    }

    @Test
    void getUserByIdWhenNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(TestData.USER_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getUserWithRoleWhenFound() {
        User user = TestData.defaultUser();
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        UserResponseWithRoleDto result = userService.getUserWithRole(TestData.USER_ID);

        assertThat(result.id()).isEqualTo(TestData.USER_ID);
        assertThat(result.roles()).isEmpty();
    }

    @Test
    void getUserWithRoleWhenNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserWithRole(TestData.USER_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getAllUsersWithSuccess() {
        User user = TestData.defaultUser();
        when(userRepository.findAllByFilter(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserResponseDto> result = userService.getAllUsers(
                TestData.PAGE, TestData.SIZE, TestData.EMPTY, TestData.EMPTY, TestData.EMPTY, TestData.EMPTY);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUserByNameWithName() {
        User user = TestData.defaultUser();
        when(userRepository.findAllByFullName(eq(TestData.FIRST_NAME_IVAN), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserShortResponseDto2> result =
                userService.getUserByName(TestData.FIRST_NAME_IVAN, TestData.PAGE, TestData.SIZE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUserByNameWhenNameIsNull() {
        when(userRepository.findAllByFullName(eq(TestData.EMPTY), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<UserShortResponseDto2> result = userService.getUserByName(null, TestData.PAGE, TestData.SIZE);

        assertThat(result).isEmpty();
    }

    @Test
    void getNamesLikeWhenFirstName() {
        when(userRepository.findDistinctByFirstNameLikeIgnoreCase(TestData.FIRST_NAME_IVAN))
                .thenReturn(List.of(TestData.FIRST_NAME_IVAN));

        List<String> result = userService.getNamesLike(TestData.FIRST_NAME_IVAN, null, null);

        assertThat(result).containsExactly(TestData.FIRST_NAME_IVAN);
    }

    @Test
    void getNamesLikeWhenLastName() {
        when(userRepository.findDistinctByLastNameLikeIgnoreCase(TestData.LAST_NAME_IVANOV))
                .thenReturn(List.of(TestData.LAST_NAME_IVANOV));

        List<String> result = userService.getNamesLike(null, TestData.LAST_NAME_IVANOV, null);

        assertThat(result).containsExactly(TestData.LAST_NAME_IVANOV);
    }

    @Test
    void getNamesLikeWhenMiddleName() {
        when(userRepository.findDistinctByMiddleNameLikeIgnoreCase(TestData.MIDDLE_NAME))
                .thenReturn(List.of(TestData.MIDDLE_NAME));

        List<String> result = userService.getNamesLike(null, null, TestData.MIDDLE_NAME);

        assertThat(result).containsExactly(TestData.MIDDLE_NAME);
    }

    @Test
    void getNamesLikeWhenAllNull() {
        List<String> result = userService.getNamesLike(null, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllUsersByIdsWithSuccess() {
        User user = TestData.defaultUser();
        when(userRepository.findByIdInOrderById(List.of(TestData.USER_ID))).thenReturn(List.of(user));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserResponseDto> result = userService.getAllUsersByIds(List.of(TestData.USER_ID));

        assertThat(result).hasSize(1);
    }

    @Test
    void loadUserByUsernameReturnsNull() {
        assertThat(userService.loadUserByUsername(TestData.EMAIL_IVANOV)).isNull();
    }

    @Test
    void getUserByNameWithRoleWithSuccess() {
        User user = TestData.defaultUser();
        when(userRepository.findAllByFullNameWithRole(eq(TestData.EMPTY), eq(Roles.STUDENT), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserShortResponseDto2> result =
                userService.getUserByNameWithRole(null, Roles.STUDENT, TestData.PAGE, TestData.SIZE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUsersByRoleWhenOnMyFloor() {
        User user = TestData.defaultUser();
        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserID()).thenReturn(TestData.USER_ID);
            when(userRepository.findRoomNumberById(TestData.USER_ID))
                    .thenReturn(Optional.of(TestData.ROOM_NUMBER_101));
            when(userRepository.findAllByFloorAndRole(Roles.STUDENT, TestData.FLOOR_1))
                    .thenReturn(List.of(user));
            when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

            List<UserShortResponseDto2> result =
                    userService.getAllUsersByRole(Roles.STUDENT, TestData.PAGE, TestData.SIZE, true);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getAllUsersByRoleWhenNotOnMyFloor() {
        User user = TestData.defaultUser();
        when(userRepository.findAllByRoles_Role(eq(Roles.STUDENT), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserShortResponseDto2> result =
                userService.getAllUsersByRole(Roles.STUDENT, TestData.PAGE, TestData.SIZE, false);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUserByNameWithoutRoleWithSuccess() {
        User user = TestData.defaultUser();
        when(userRepository.findAllByFullNameWithoutRole(eq(TestData.EMPTY), eq(Roles.STUDENT), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserShortResponseDto2> result =
                userService.getUserByNameWithoutRole(null, Roles.STUDENT, TestData.PAGE, TestData.SIZE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUsersByIdsShortWithSuccess() {
        User user = TestData.defaultUser();
        when(userRepository.findAllById(List.of(TestData.USER_ID))).thenReturn(List.of(user));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserShortResponseDto2> result = userService.getAllUsersByIdsShort(List.of(TestData.USER_ID));

        assertThat(result).hasSize(1);
    }

    @Test
    void getUserByIdShortWhenFound() {
        User user = TestData.defaultUser();
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        UserShortResponseDto2 result = userService.getUserByIdShort(TestData.USER_ID);

        assertThat(result.id()).isEqualTo(TestData.USER_ID);
    }

    @Test
    void getUserByIdShortWhenNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByIdShort(TestData.USER_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getAllUsersOnFloorWhenFound() {
        User user = TestData.defaultUser();
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findAllByRoomNumberStartingWithOrderByRoomNumber(eq(TestData.FLOOR_1), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(contactRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        List<UserNameResponseDto> result =
                userService.getAllUsersOnFloor(TestData.USER_ID, TestData.PAGE, TestData.SIZE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllUsersOnFloorWhenNotFound() {
        when(userRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.getAllUsersOnFloor(TestData.USER_ID, TestData.PAGE, TestData.SIZE))
                .isInstanceOf(NotFound.class);
    }

    @Test
    void getRoomNumberByUserIdWhenFound() {
        when(userRepository.findRoomNumberById(TestData.USER_ID)).thenReturn(Optional.of(TestData.ROOM_NUMBER_101));

        assertThat(userService.getRoomNumberByUserId(TestData.USER_ID)).isEqualTo(TestData.ROOM_NUMBER_101);
    }

    @Test
    void getRoomNumberByUserIdWhenNotFound() {
        when(userRepository.findRoomNumberById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getRoomNumberByUserId(TestData.USER_ID)).isInstanceOf(NotFound.class);
    }

    @Test
    void getAllUsersInRoomsWithSuccess() {
        User user = TestData.defaultUser();
        Contact contact = TestData.newContact(TestData.CONTACT_ID, TestData.EMAIL_IVANOV);
        when(userRepository.findAllByRoomNumberInOrderByRoomNumber(List.of(TestData.ROOM_NUMBER_101)))
                .thenReturn(List.of(user));
        when(contactRepository.findFirstByEmail(TestData.EMAIL_IVANOV)).thenReturn(Optional.of(contact));

        List<UserNameResponseDto> result = userService.getAllUsersInRooms(List.of(TestData.ROOM_NUMBER_101));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).tgLink()).isEqualTo(TestData.TG_LINK);
    }

    @Test
    void getAllIdsOfUsersInRoomsWithSuccess() {
        Set<String> rooms = Set.of(TestData.ROOM_NUMBER_101);
        when(userRepository.findAllIdsOfUsersInRooms(List.copyOf(rooms))).thenReturn(List.of(TestData.USER_ID));

        List<UUID> result = userService.getAllIdsOfUsersInRooms(List.copyOf(rooms));

        assertThat(result).containsExactly(TestData.USER_ID);
    }
}
