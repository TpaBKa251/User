package ru.tpu.hostel.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.user.entity.Contact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("SELECT c FROM Contact c WHERE c.customContact = true ")
    List<Contact> getAllCustomContacts();

    @Query("SELECT c FROM Contact c WHERE c.role in :selectedRoles")
    List<Contact> getAllMainContacts(@Param("selectedRoles") List<String> selectedRoles);

    Optional<Contact> findFirstByEmail(String email);

    List<Contact> findAllByEmail(String email);

    @Modifying
    @Query("""
        UPDATE Contact c
        SET c.tgLink = :tgLink
        WHERE c.id = :contactId
    """)
    void updateTgLink(UUID contactId, String tgLink);

    @Modifying
    @Query("""
        UPDATE Contact c
        SET c.vkLink = :vkLink
        WHERE c.id = :contactId
    """)
    void updateVkLink(UUID contactId, String vkLink);

}
