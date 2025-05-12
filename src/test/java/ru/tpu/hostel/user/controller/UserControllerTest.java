package ru.tpu.hostel.user.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.Data;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.repository.util.PostgresTestContainerExtension;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Интеграционные тесты контроллера сессии SessionController")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(PostgresTestContainerExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    private List<UserShortResponseDto2> adminUsers;

    @BeforeEach
    void setUp() {
        adminUsers = List.of(
                new UserShortResponseDto2(
                        UUID.randomUUID(),
                        Data.FIRST_NAME_IVAN,
                        Data.LAST_NAME_IVANOV,
                        null),
                new UserShortResponseDto2(
                        UUID.randomUUID(),
                        Data.FIRST_NAME_LEONID,
                        Data.LAST_NAME_LEONIDOV,
                        null)
        );
    }

    @AfterEach
    void commonVerify() {
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("Получение списка пользователей по роли")
    void testGetUsersByRole_Success() throws Exception {
        Roles role = Roles.ADMINISTRATION;
        int page = 0;
        int size = 10;

        when(userService.getAllUsersByRole(role, page, size)).thenReturn(adminUsers);

        mockMvc.perform(get("/users/get/by/role")
                        .param("role", role.name())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(adminUsers.size()))
                .andExpect(jsonPath("$[0].id").value(adminUsers.get(0).id().toString()))
                .andExpect(jsonPath("$[0].firstName").value(adminUsers.get(0).firstName()))
                .andExpect(jsonPath("$[0].lastName").value(adminUsers.get(0).lastName()))
                .andExpect(jsonPath("$[0].middleName").value(adminUsers.get(0).middleName()))
                .andExpect(jsonPath("$[1].id").value(adminUsers.get(1).id().toString()))
                .andExpect(jsonPath("$[1].firstName").value(adminUsers.get(1).firstName()))
                .andExpect(jsonPath("$[1].lastName").value(adminUsers.get(1).lastName()))
                .andExpect(jsonPath("$[1].middleName").value(adminUsers.get(1).middleName()));

        verify(userService, times(1)).getAllUsersByRole(role, page, size);
    }

    @Test
    @DisplayName("Получение списка пользователей по роли (пустой список)")
    void testGetUsersByRole_EmptyList() throws Exception {
        Roles role = Roles.STUDENT;
        int page = 0;
        int size = 10;

        when(userService.getAllUsersByRole(role, page, size)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/get/by/role")
                        .param("role", role.name())
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsersByRole(role, page, size);
    }
}
