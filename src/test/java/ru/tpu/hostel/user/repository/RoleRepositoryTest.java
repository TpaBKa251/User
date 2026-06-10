package ru.tpu.hostel.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.TestData;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.repository.util.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Role role;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();

        User newUser = new User();
        newUser.setFirstName(TestData.FIRST_NAME_IVAN);
        newUser.setLastName(TestData.LAST_NAME_IVANOV);
        newUser.setEmail(TestData.EMAIL_IVANOV);
        newUser.setPassword(TestData.PASSWORD_ENCODED);
        newUser.setRoomNumber(TestData.ROOM_NUMBER_101);
        user = userRepository.save(newUser);

        Role newRole = new Role();
        newRole.setRole(Roles.STUDENT);
        newRole.setUser(user);
        role = roleRepository.save(newRole);
    }

    @Test
    void findByUserWithSuccess() {
        List<Role> result = roleRepository.findByUser(user);

        assertThat(result).extracting(Role::getRole).containsExactly(Roles.STUDENT);
    }

    @Test
    void findByRoleWithSuccess() {
        List<Role> result = roleRepository.findByRole(Roles.STUDENT);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByRoleAndUserWithSuccess() {
        Optional<Role> result = roleRepository.findByRoleAndUser(Roles.STUDENT, user);

        assertThat(result).isPresent();
    }

    @Test
    void findByIdOptimisticWithSuccess() {
        Optional<Role> result = roleRepository.findByIdOptimistic(role.getId());

        assertThat(result).isPresent();
    }

    @Test
    void deleteByUserAndRoleWithSuccess() {
        roleRepository.deleteByUserAndRole(user, Roles.STUDENT);

        assertThat(roleRepository.findByRole(Roles.STUDENT)).isEmpty();
    }
}
