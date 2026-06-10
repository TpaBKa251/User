package ru.tpu.hostel.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.entity.Session;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.util.RepositoryTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private Session session;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setFirstName(TestData.FIRST_NAME_IVAN);
        user.setLastName(TestData.LAST_NAME_IVANOV);
        user.setEmail(TestData.EMAIL_IVANOV);
        user.setPassword(TestData.PASSWORD_ENCODED);
        user.setRoomNumber(TestData.ROOM_NUMBER_101);
        user = userRepository.save(user);

        Session newSession = new Session();
        newSession.setUserId(user);
        newSession.setCreateTime(LocalDateTime.now());
        newSession.setExpirationTime(LocalDateTime.now().plusDays(30));
        newSession.setRefreshToken(TestData.REFRESH_TOKEN);
        session = sessionRepository.save(newSession);
    }

    @Test
    void findByRefreshTokenWithSuccess() {
        Optional<Session> result = sessionRepository.findByRefreshToken(TestData.REFRESH_TOKEN);

        assertThat(result).isPresent();
    }

    @Test
    void findByRefreshTokenWhenNotExists() {
        Optional<Session> result = sessionRepository.findByRefreshToken("missing-token");

        assertThat(result).isEmpty();
    }

    @Test
    void findByIdOptimisticWithSuccess() {
        Optional<Session> result = sessionRepository.findByIdOptimistic(session.getId());

        assertThat(result).isPresent();
    }
}
