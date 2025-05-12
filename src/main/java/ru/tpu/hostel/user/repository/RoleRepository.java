package ru.tpu.hostel.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByUser(User user);

    List<Role> findByRole(Roles role);

    Optional<Role> findByRoleAndUser(Roles role, User user);

    @Transactional
    void deleteByUserAndRole(User user, Roles role);
}
