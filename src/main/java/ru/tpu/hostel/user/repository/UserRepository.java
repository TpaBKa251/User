package ru.tpu.hostel.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository()
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    @Query("select distinct u.firstName from User u where lower(u.firstName) like lower(concat('%', :firstName, '%'))")
    List<String> findDistinctByFirstNameLikeIgnoreCase(@Param("firstName") String firstName);

    @Query("select distinct u.lastName from User u where lower(u.lastName) like lower(concat('%', :lastName, '%'))")
    List<String> findDistinctByLastNameLikeIgnoreCase(@Param("lastName") String lastName);

    @Query("select distinct u.middleName from User u where lower(u.middleName) like lower(concat('%', :middleName, '%'))")
    List<String> findDistinctByMiddleNameLikeIgnoreCase(@Param("middleName") String middleName);
}
