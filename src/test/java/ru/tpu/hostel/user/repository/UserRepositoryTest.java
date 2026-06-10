package ru.tpu.hostel.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.util.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User ivanov;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        ivanov = userRepository.save(buildUser(
                TestData.FIRST_NAME_IVAN, TestData.LAST_NAME_IVANOV, TestData.EMAIL_IVANOV, TestData.ROOM_NUMBER_101));
        userRepository.save(buildUser(
                TestData.FIRST_NAME_BOGDAN, TestData.LAST_NAME_BOGDANOV, TestData.EMAIL_BOGDANOV, TestData.ROOM_NUMBER_201));

        Role role = new Role();
        role.setRole(Roles.STUDENT);
        role.setUser(ivanov);
        roleRepository.save(role);
    }

    private User buildUser(String firstName, String lastName, String email, String roomNumber) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(TestData.PASSWORD_ENCODED);
        user.setRoomNumber(roomNumber);
        return user;
    }

    @Test
    void findByEmailWhenExists() {
        Optional<User> result = userRepository.findByEmail(TestData.EMAIL_IVANOV);

        assertThat(result).isPresent();
        assertThat(result.get().getLastName()).isEqualTo(TestData.LAST_NAME_IVANOV);
    }

    @Test
    void findByEmailWhenNotExists() {
        Optional<User> result = userRepository.findByEmail(TestData.EMAIL_LEONIDOV);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByRolesRoleWithSuccess() {
        Pageable pageable = PageRequest.of(TestData.PAGE, TestData.SIZE);

        var result = userRepository.findAllByRoles_Role(Roles.STUDENT, pageable);

        assertThat(result.getContent())
                .extracting(User::getEmail)
                .containsExactly(TestData.EMAIL_IVANOV);
    }

    @Test
    void findRoomNumberByIdWithSuccess() {
        Optional<String> result = userRepository.findRoomNumberById(ivanov.getId());

        assertThat(result).contains(TestData.ROOM_NUMBER_101);
    }

    @Test
    void findAllIdsOfUsersInRoomsWithSuccess() {
        List<java.util.UUID> result = userRepository.findAllIdsOfUsersInRooms(List.of(TestData.ROOM_NUMBER_101));

        assertThat(result).containsExactly(ivanov.getId());
    }

    @Test
    void findDistinctByFirstNameLikeIgnoreCaseWithSuccess() {
        List<String> result = userRepository.findDistinctByFirstNameLikeIgnoreCase(TestData.FIRST_NAME_IVAN);

        assertThat(result).contains(TestData.FIRST_NAME_IVAN);
    }
}
