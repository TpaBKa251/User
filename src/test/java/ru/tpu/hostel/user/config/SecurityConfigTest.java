package ru.tpu.hostel.user.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(mock(UserServiceImpl.class));

    @Test
    void passwordEncoderWithSuccess() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();

        assertThat(encoder.matches(TestData.PASSWORD_RAW, encoder.encode(TestData.PASSWORD_RAW))).isTrue();
    }

    @Test
    void daoAuthenticationProviderWithSuccess() {
        DaoAuthenticationProvider provider = securityConfig.daoAuthenticationProvider();

        assertThat(provider).isNotNull();
    }
}
