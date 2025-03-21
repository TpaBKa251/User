package ru.tpu.hostel.user.repository;

import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.enums.Roles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Validated
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query("select distinct u.firstName from User u where lower(u.firstName) like lower(concat('%', :firstName, '%'))")
    List<String> findDistinctByFirstNameLikeIgnoreCase(@Param("firstName") String firstName);

    @Query("select distinct u.lastName from User u where lower(u.lastName) like lower(concat('%', :lastName, '%'))")
    List<String> findDistinctByLastNameLikeIgnoreCase(@Param("lastName") String lastName);

    @Query("select distinct u.middleName from User u where lower(u.middleName) like lower(concat('%', :middleName, '%'))")
    List<String> findDistinctByMiddleNameLikeIgnoreCase(@Param("middleName") String middleName);

    @Query("""
                SELECT u
                FROM User u
                WHERE (:firstName LIKE '' OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%') ))
                  AND (:lastName LIKE '' OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%') ) )
                  AND (:middleName LIKE '' OR LOWER(u.middleName) LIKE LOWER(CONCAT('%', :middleName, '%') ) )
                  AND (:roomNumber LIKE '' OR LOWER(u.roomNumber) LIKE LOWER(CONCAT('%', :roomNumber, '%') ) )
            """)
    Page<User> findAllByFilter(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("middleName") String middleName,
            @Param("roomNumber") String roomNumber,
            Pageable pageable
    );

    List<User> findByIdInOrderById(List<UUID> ids);


    @Query("SELECT u FROM User u " +
            "WHERE LOWER(CONCAT(u.lastName, ' ', u.firstName, ' ', COALESCE(u.middleName, ''))) " +
            "LIKE LOWER(CONCAT('%', :fullName, '%'))")
    Page<User> findAllByFullName(@Param("fullName") String fullName, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.roles r " +
            "WHERE LOWER(CONCAT(u.lastName, ' ', u.firstName, ' ', COALESCE(u.middleName, ''))) " +
            "LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND (r.role IS NULL OR r.role != :role)")
    Page<User> findAllByFullNameWithoutRole(@Param("fullName") String fullName, @Param("role") Roles role, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE LOWER(CONCAT(u.lastName, ' ', u.firstName, ' ', COALESCE(u.middleName, ''))) " +
            "LIKE LOWER(CONCAT('%', :fullName, '%')) " +
            "AND r.role = :role")
    Page<User> findAllByFullNameWithRole(@Param("fullName") String fullName, @Param("role") Roles role, Pageable pageable);

    Page<User> findAllByRoles_Role(@Param("role") Roles role, Pageable pageable);

    @Query("select u.roomNumber from User u where u.id = :id")
    Optional<String> findRoomNumberById(@Param("id") UUID id);

    Page<User> findAllByRoomNumberStartingWithOrderByRoomNumber(
            @Pattern(regexp = "\\d", message = "Этаж должен быть одной цифрой") String floor,
            Pageable pageable
    );

    List<User> findAllByRoomNumberInOrderByRoomNumber(List<String> roomNumbers);

    @Query("select distinct u.id from User u where u.roomNumber in :roomNumbers")
    List<UUID> findAllIdsOfUsersInRooms(@Param("roomNumbers") List<String> roomNumbers);
}