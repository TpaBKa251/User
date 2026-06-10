package ru.tpu.hostel.user.service;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.dto.response.SessionRefreshResponse;
import ru.tpu.hostel.user.dto.response.SessionResponseDto;
import ru.tpu.hostel.user.entity.Session;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.jwt.JwtService;
import ru.tpu.hostel.user.repository.SessionRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.impl.SessionServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.tpu.hostel.internal.exception.ServiceException.Conflict;
import static ru.tpu.hostel.internal.exception.ServiceException.Forbidden;
import static ru.tpu.hostel.internal.exception.ServiceException.NotFound;
import static ru.tpu.hostel.internal.exception.ServiceException.Unauthorized;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void loginWithSuccess() {
        User user = TestData.defaultUser();
        when(userRepository.findByEmail(TestData.EMAIL_IVANOV)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestData.PASSWORD_RAW, TestData.PASSWORD_ENCODED)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(TestData.ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(user)).thenReturn(TestData.REFRESH_TOKEN);
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            Session session = invocation.getArgument(0);
            session.setId(TestData.SESSION_ID);
            return session;
        });

        SessionResponseDto result = sessionService.login(TestData.sessionLoginDto(), response);

        assertThat(result.id()).isEqualTo(TestData.SESSION_ID);
        assertThat(result.accessToken()).isEqualTo(TestData.ACCESS_TOKEN);
        verify(response).addHeader(anyString(), anyString());
    }

    @Test
    void loginWhenPasswordIsIncorrect() {
        User user = TestData.defaultUser();
        when(userRepository.findByEmail(TestData.EMAIL_IVANOV)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestData.PASSWORD_RAW, TestData.PASSWORD_ENCODED)).thenReturn(false);

        assertThatThrownBy(() -> sessionService.login(TestData.sessionLoginDto(), response))
                .isInstanceOf(Unauthorized.class);
    }

    @Test
    void loginWhenUserNotFound() {
        when(userRepository.findByEmail(TestData.EMAIL_IVANOV)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.login(TestData.sessionLoginDto(), response))
                .isInstanceOf(Unauthorized.class);
    }

    @Test
    void logoutWithSuccess() {
        User user = TestData.defaultUser();
        Session session = TestData.newSession(user, TestData.REFRESH_TOKEN, LocalDateTime.now().plusDays(1));
        when(sessionRepository.findByIdOptimistic(TestData.SESSION_ID)).thenReturn(Optional.of(session));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserID()).thenReturn(TestData.USER_ID);

            sessionService.logout(TestData.SESSION_ID, response);

            assertThat(session.getRefreshToken()).isNull();
            verify(sessionRepository).flush();
        }
    }

    @Test
    void logoutWhenSessionNotFound() {
        when(sessionRepository.findByIdOptimistic(TestData.SESSION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.logout(TestData.SESSION_ID, response))
                .isInstanceOf(NotFound.class);
    }

    @Test
    void logoutWhenForeignSession() {
        User user = TestData.defaultUser();
        Session session = TestData.newSession(user, TestData.REFRESH_TOKEN, LocalDateTime.now().plusDays(1));
        when(sessionRepository.findByIdOptimistic(TestData.SESSION_ID)).thenReturn(Optional.of(session));

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserID()).thenReturn(TestData.OTHER_USER_ID);

            assertThatThrownBy(() -> sessionService.logout(TestData.SESSION_ID, response))
                    .isInstanceOf(Forbidden.class);
        }
    }

    @Test
    void logoutWhenOptimisticLockFailure() {
        User user = TestData.defaultUser();
        Session session = TestData.newSession(user, TestData.REFRESH_TOKEN, LocalDateTime.now().plusDays(1));
        when(sessionRepository.findByIdOptimistic(TestData.SESSION_ID)).thenReturn(Optional.of(session));
        doThrow(new ObjectOptimisticLockingFailureException(Session.class, TestData.SESSION_ID))
                .when(sessionRepository).flush();

        try (MockedStatic<ExecutionContext> contextMock = mockStatic(ExecutionContext.class)) {
            ExecutionContext context = mock(ExecutionContext.class);
            contextMock.when(ExecutionContext::get).thenReturn(context);
            when(context.getUserID()).thenReturn(TestData.USER_ID);

            assertThatThrownBy(() -> sessionService.logout(TestData.SESSION_ID, response))
                    .isInstanceOf(Conflict.class);
        }
    }

    @Test
    void refreshWithSuccess() {
        User user = TestData.defaultUser();
        Session session = TestData.newSession(user, TestData.REFRESH_TOKEN, LocalDateTime.now().plusDays(1));
        when(sessionRepository.findByRefreshToken(TestData.REFRESH_TOKEN)).thenReturn(Optional.of(session));
        when(jwtService.generateAccessToken(user)).thenReturn(TestData.ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(user)).thenReturn(TestData.REFRESH_TOKEN);

        SessionRefreshResponse result = sessionService.refresh(TestData.REFRESH_TOKEN, response);

        assertThat(result.token()).isEqualTo(TestData.ACCESS_TOKEN);
        verify(jwtService).checkRefreshTokenValidity(TestData.REFRESH_TOKEN);
        verify(response).setHeader(anyString(), anyString());
    }

    @Test
    void refreshWhenSessionNotFound() {
        when(sessionRepository.findByRefreshToken(TestData.REFRESH_TOKEN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.refresh(TestData.REFRESH_TOKEN, response))
                .isInstanceOf(NotFound.class);
    }

    @Test
    void refreshWhenSessionExpired() {
        User user = TestData.defaultUser();
        Session session = TestData.newSession(user, TestData.REFRESH_TOKEN, LocalDateTime.now().minusDays(1));
        when(sessionRepository.findByRefreshToken(TestData.REFRESH_TOKEN)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> sessionService.refresh(TestData.REFRESH_TOKEN, response))
                .isInstanceOf(Forbidden.class);
    }

    @Test
    void refreshWhenOptimisticLockFailure() {
        User user = TestData.defaultUser();
        Session session = TestData.newSession(user, TestData.REFRESH_TOKEN, LocalDateTime.now().plusDays(1));
        when(sessionRepository.findByRefreshToken(TestData.REFRESH_TOKEN)).thenReturn(Optional.of(session));
        when(jwtService.generateAccessToken(user)).thenReturn(TestData.ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(user)).thenReturn(TestData.REFRESH_TOKEN);
        doThrow(new ObjectOptimisticLockingFailureException(Session.class, TestData.SESSION_ID))
                .when(sessionRepository).flush();

        assertThatThrownBy(() -> sessionService.refresh(TestData.REFRESH_TOKEN, response))
                .isInstanceOf(Conflict.class);
    }
}
