package ru.tpu.hostel.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.user.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("SELECT c FROM Contact c WHERE c.role not in :excludedRoles")
    List<Contact> getAllMainContacts(@Param("excludedRoles") List<String> excludedRoles);

    Optional<Contact> findFirstByEmailOrderByVersionDesc(String email);
}
