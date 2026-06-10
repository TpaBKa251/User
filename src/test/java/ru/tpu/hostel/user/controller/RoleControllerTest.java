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
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.service.RoleService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    private RoleResponseDto roleResponseDto() {
        return new RoleResponseDto(TestData.ROLE_ID, TestData.USER_ID, Roles.STUDENT.getRoleName());
    }

    @Test
    void setRoleToUserWithSuccess() throws Exception {
        when(roleService.setRole(any())).thenReturn(roleResponseDto());

        mockMvc.perform(post("/roles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                TestData.roleSetDto(TestData.USER_ID, Roles.HOSTEL_SUPERVISOR))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TestData.ROLE_ID.toString()));
    }

    @Test
    void editRoleWithSuccess() throws Exception {
        when(roleService.editRole(any())).thenReturn(roleResponseDto());

        mockMvc.perform(patch("/roles/edit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                TestData.roleEditDto(TestData.ROLE_ID, Roles.FLOOR_SUPERVISOR))))
                .andExpect(status().isOk());
    }

    @Test
    void getRoleWithSuccess() throws Exception {
        when(roleService.getRole(TestData.ROLE_ID)).thenReturn(roleResponseDto());

        mockMvc.perform(get("/roles/get/{id}", TestData.ROLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TestData.ROLE_ID.toString()));
    }

    @Test
    void getRoleByUserIdWithSuccess() throws Exception {
        when(roleService.getUserRoles(TestData.USER_ID)).thenReturn(List.of(roleResponseDto()));

        mockMvc.perform(get("/roles/get/user/roles/{userId}", TestData.USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteRoleWithSuccess() throws Exception {
        mockMvc.perform(delete("/roles/{userId}", TestData.USER_ID)
                        .param("role", Roles.HOSTEL_SUPERVISOR.name()))
                .andExpect(status().isNoContent());
    }
}
