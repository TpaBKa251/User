package ru.tpu.hostel.user.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.UserRepository;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static ru.tpu.hostel.internal.exception.ServiceException.Unauthorized;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", TestData.JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", Duration.ofMinutes(15));
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", Duration.ofHours(720));
    }

    @Test
    void generateAccessTokenWithSuccess() {
        User user = TestData.defaultUser();

        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.getUserIdFromToken(token)).isEqualTo(TestData.USER_ID);
    }

    @Test
    void generateRefreshTokenWithSuccess() {
        User user = TestData.defaultUser();

        String token = jwtService.generateRefreshToken(user);

        assertThat(token).isNotBlank();
    }

    @Test
    void getUserIdFromTokenWithSuccess() {
        User user = TestData.defaultUser();
        String token = jwtService.generateRefreshToken(user);

        UUID userId = jwtService.getUserIdFromToken(token);

        assertThat(userId).isEqualTo(TestData.USER_ID);
    }

    @Test
    void checkRefreshTokenValidityWithSuccess() {
        User user = TestData.defaultUser();
        String token = jwtService.generateRefreshToken(user);
        lenient().when(userRepository.existsById(TestData.USER_ID)).thenReturn(true);

        jwtService.checkRefreshTokenValidity(token);
    }

    @Test
    void checkRefreshTokenValidityWhenTokenIsEmpty() {
        assertThatThrownBy(() -> jwtService.checkRefreshTokenValidity(TestData.EMPTY))
                .isInstanceOf(Unauthorized.class);
    }

    @Test
    void checkRefreshTokenValidityWhenTokenIsNull() {
        assertThatThrownBy(() -> jwtService.checkRefreshTokenValidity(null))
                .isInstanceOf(Unauthorized.class);
    }

    @Test
    void checkRefreshTokenValidityWhenUserNotFound() {
        User user = TestData.defaultUser();
        String token = jwtService.generateRefreshToken(user);
        lenient().when(userRepository.existsById(TestData.USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> jwtService.checkRefreshTokenValidity(token))
                .isInstanceOf(Unauthorized.class);
    }

    @Test
    void checkRefreshTokenValidityWhenTokenIsMalformed() {
        assertThatThrownBy(() -> jwtService.checkRefreshTokenValidity("not-a-valid-token"))
                .isInstanceOf(Unauthorized.class);
    }
}
