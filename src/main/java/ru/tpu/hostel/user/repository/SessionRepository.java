package ru.tpu.hostel.user.repository;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.user.entity.Session;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Session> findByRefreshToken(String refreshToken);

    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM Session s WHERE s.id = :id")
    Optional<Session> findByIdOptimistic(@Param("id") UUID id);
}
