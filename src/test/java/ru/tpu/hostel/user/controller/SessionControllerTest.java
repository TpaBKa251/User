package ru.tpu.hostel.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.SessionRefreshResponse;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;
import ru.tpu.hostel.user.service.SessionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessionService sessionService;

    @Test
    void loginWithSuccess() throws Exception {
        when(sessionService.login(any(), any()))
                .thenReturn(new SessionResponseDto(TestData.SESSION_ID, TestData.ACCESS_TOKEN));

        mockMvc.perform(post("/sessions")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.sessionLoginDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(TestData.ACCESS_TOKEN));
    }

    @Test
    void logoutWithSuccess() throws Exception {
        mockMvc.perform(patch("/sessions/logout/{sessionId}", TestData.SESSION_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void refreshTokenWithSuccess() throws Exception {
        when(sessionService.refresh(any(), any()))
                .thenReturn(new SessionRefreshResponse(TestData.ACCESS_TOKEN));

        mockMvc.perform(get("/sessions/auth/token")
                        .cookie(new Cookie("refreshToken", TestData.REFRESH_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TestData.ACCESS_TOKEN));
    }

    @Test
    void refreshTokenPostWithSuccess() throws Exception {
        when(sessionService.refresh(any(), any()))
                .thenReturn(new SessionRefreshResponse(TestData.ACCESS_TOKEN));

        mockMvc.perform(post("/sessions/auth/token")
                        .cookie(new Cookie("refreshToken", TestData.REFRESH_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(TestData.ACCESS_TOKEN));
    }
}
