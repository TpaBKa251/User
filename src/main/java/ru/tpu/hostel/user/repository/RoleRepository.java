package ru.tpu.hostel.user.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Transactional
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Role> findByIdOptimistic(@Param("id") UUID id);
}
