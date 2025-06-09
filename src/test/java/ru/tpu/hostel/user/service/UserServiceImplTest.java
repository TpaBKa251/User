package ru.tpu.hostel.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.Data;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса пользователей UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private List<User> adminUsers;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by("id"));

        User adminUser1 = Data.getNewUser(
                Data.FIRST_NAME_IVAN,
                Data.LAST_NAME_IVANOV,
                Data.EMAIL_IVANOV,
                Data.PASSWORD_IVANOV,
                Data.ROOM_NUMBER_101
        );
        User adminUser2 = Data.getNewUser(
                Data.FIRST_NAME_LEONID,
                Data.LAST_NAME_LEONIDOV,
                Data.EMAIL_LEONIDOV,
                Data.PASSWORD_LEONIDOV,
                Data.ROOM_NUMBER_101
        );

        adminUsers = List.of(adminUser1, adminUser2);
    }

    @AfterEach
    void commonVerify() {
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Тест получения всех пользователей по роли")
    void testGetAllUsersByRole_AdminRole() {
        Roles role = Roles.ADMINISTRATION;
        Page<User> mockPage = new PageImpl<>(adminUsers);

        when(userRepository.findAllByRoles_Role(role, pageable)).thenReturn(mockPage);

        List<UserShortResponseDto2> result = userService.getAllUsersByRole(role, 0, 10, false);

        assertThat(result)
                .hasSize(2)
                .extracting(UserShortResponseDto2::id, UserShortResponseDto2::firstName, UserShortResponseDto2::lastName, UserShortResponseDto2::middleName)
                .containsExactlyInAnyOrder(
                        tuple(
                                adminUsers.get(0).getId(),
                                adminUsers.get(0).getFirstName(),
                                adminUsers.get(0).getLastName(),
                                adminUsers.get(0).getMiddleName()
                        ),
                        tuple(
                                adminUsers.get(1).getId(),
                                adminUsers.get(1).getFirstName(),
                                adminUsers.get(1).getLastName(),
                                adminUsers.get(1).getMiddleName()
                        )
                );

        verify(userRepository, times(1)).findAllByRoles_Role(role, pageable);
    }

    @Test
    @DisplayName("Тест получения всех пользователей по роли, когда нет результатов")
    void testGetAllUsersByRole_EmptyResult() {
        Roles role = Roles.RESPONSIBLE_KITCHEN;

        when(userRepository.findAllByRoles_Role(role, pageable)).thenReturn(Page.empty());

        List<UserShortResponseDto2> result = userService.getAllUsersByRole(role, 0, 10, false);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findAllByRoles_Role(role, pageable);
    }
}
