package ru.tpu.hostel.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userService;

    private UserResponseDto userResponseDto() {
        return new UserResponseDto(
                TestData.USER_ID,
                TestData.FIRST_NAME_IVAN,
                TestData.LAST_NAME_IVANOV,
                TestData.MIDDLE_NAME,
                TestData.EMAIL_IVANOV,
                TestData.PHONE,
                TestData.ROOM_NUMBER_101,
                TestData.TG_LINK,
                TestData.VK_LINK
        );
    }

    @Test
    void registerUserWithSuccess() throws Exception {
        when(userService.registerUser(any())).thenReturn(userResponseDto());

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.userRegisterDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TestData.USER_ID.toString()));
    }

    @Test
    void getUserWithSuccess() throws Exception {
        when(userService.getUser()).thenReturn(userResponseDto());

        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TestData.EMAIL_IVANOV));
    }

    @Test
    void getAllUsersWithSuccess() throws Exception {
        when(userService.getAllUsers(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(List.of(userResponseDto()));

        mockMvc.perform(get("/users/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRoomNumberWithSuccess() throws Exception {
        when(userService.getRoomNumberByUserId(TestData.USER_ID)).thenReturn(TestData.ROOM_NUMBER_101);

        mockMvc.perform(get("/users/get/room/{userId}", TestData.USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByRoleWithSuccess() throws Exception {
        UserShortResponseDto2 dto = new UserShortResponseDto2(
                TestData.USER_ID, TestData.FIRST_NAME_IVAN, TestData.LAST_NAME_IVANOV,
                TestData.MIDDLE_NAME, TestData.TG_LINK, TestData.VK_LINK);
        when(userService.getAllUsersByRole(eq(Roles.STUDENT), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/users/get/by/role")
                        .param("role", Roles.STUDENT.name())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
