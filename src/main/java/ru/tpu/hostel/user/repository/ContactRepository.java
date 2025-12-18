package ru.tpu.hostel.user.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("SELECT c FROM Contact c WHERE c.role not in :excludedRoles")
    List<Contact> getAllMainContacts(@Param("excludedRoles") List<String> excludedRoles);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Contact> findByEmailOptimistic(String email);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select с from Contact с where с.id = :id")
    Optional<User> findByIdOptimistic(@Param("id") UUID id);

//    @Modifying
//    @Query(value = """
//        INSERT INTO "user"."main_contacts" (
//            id,
//            first_name,
//            last_name,
//            middle_name,
//            role,
//            email,
//            tg_link,
//            vk_link
//        )
//        SELECT
//            gen_random_uuid(),
//            u.id,
//            u.first_name,
//            u.last_name,
//            u.middle_name,
//            r.role,
//            u.email,
//            u.tg_link,
//            u.vk_link,
//            'USER'
//        FROM user.users u
//        JOIN user.roles r ON r."user" = u.id
//        ON CONFLICT (user_id, role)
//        DO UPDATE SET
//            first_name  = EXCLUDED.first_name,
//            last_name   = EXCLUDED.last_name,
//            middle_name = EXCLUDED.middle_name,
//            email       = EXCLUDED.email,
//            tg_link     = EXCLUDED.tg_link,
//            vk_link     = EXCLUDED.vk_link
//    """, nativeQuery = true)
//    void syncUserContacts();
}
