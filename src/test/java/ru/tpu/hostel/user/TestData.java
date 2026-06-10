package ru.tpu.hostel.user;

import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.AddLinkRequestDto;
import ru.tpu.hostel.user.dto.request.ContactAddRequestDto;
import ru.tpu.hostel.user.dto.request.LinkType;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.request.SessionLoginDto;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.Session;
import ru.tpu.hostel.user.entity.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

public final class TestData {

    public static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    public static final UUID ROLE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public static final UUID CONTACT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    public static final UUID SESSION_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    public static final String FIRST_NAME_IVAN = "Иван";

    public static final String FIRST_NAME_BOGDAN = "Богдан";

    public static final String FIRST_NAME_LEONID = "Леонид";

    public static final String LAST_NAME_IVANOV = "Иванов";

    public static final String LAST_NAME_BOGDANOV = "Богданов";

    public static final String LAST_NAME_LEONIDOV = "Леонидов";

    public static final String MIDDLE_NAME = "Иванович";

    public static final String EMAIL_IVANOV = "ivanov@example.com";

    public static final String EMAIL_BOGDANOV = "bogdanov@example.com";

    public static final String EMAIL_LEONIDOV = "leonidov@example.com";

    public static final String PASSWORD_RAW = "password123";

    public static final String PASSWORD_ENCODED = "$2a$10$encodedpasswordhashvaluehereforthetests1234567890abcd";

    public static final String PHONE = "79137235412";

    public static final String ROOM_NUMBER_101 = "101";

    public static final String ROOM_NUMBER_102 = "102";

    public static final String ROOM_NUMBER_201 = "201";

    public static final String FLOOR_1 = "1";

    public static final String TG_LINK = "kik_butovski";

    public static final String VK_LINK = "vk_butovski";

    public static final String TG_LINK_WITH_AT = "@kik_butovski";

    public static final String VK_LINK_FULL = "https://vk.com/vk_butovski";

    public static final String REFRESH_TOKEN = "refresh-token-value";

    public static final String ACCESS_TOKEN = "access-token-value";

    public static final String JWT_SECRET = "test-secret-key-for-hs512-algorithm-that-is-long-enough-1234567890-abcdefghij";

    public static final String JWT_ACCESS_LIFETIME = "PT15M";

    public static final String JWT_REFRESH_LIFETIME = "PT720H";

    public static final int PAGE = 0;

    public static final int SIZE = 10;

    public static final String EMPTY = "";

    private TestData() {
    }

    public static User newUser(
            UUID id,
            String firstName,
            String lastName,
            String email,
            String roomNumber
    ) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(MIDDLE_NAME);
        user.setEmail(email);
        user.setPhone(PHONE);
        user.setPassword(PASSWORD_ENCODED);
        user.setRoomNumber(roomNumber);
        user.setRoles(new HashSet<>());
        user.setVersion(0L);
        return user;
    }

    public static User defaultUser() {
        return newUser(USER_ID, FIRST_NAME_IVAN, LAST_NAME_IVANOV, EMAIL_IVANOV, ROOM_NUMBER_101);
    }

    public static Role newRole(UUID id, Roles roleType, User user) {
        Role role = new Role();
        role.setId(id);
        role.setRole(roleType);
        role.setUser(user);
        role.setVersion(0L);
        return role;
    }

    public static Contact newContact(UUID id, String email) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setFirstName(FIRST_NAME_IVAN);
        contact.setLastName(LAST_NAME_IVANOV);
        contact.setMiddleName(MIDDLE_NAME);
        contact.setRole(Roles.STUDENT.toString());
        contact.setEmail(email);
        contact.setTgLink(TG_LINK);
        contact.setVkLink(VK_LINK);
        contact.setCustomContact(true);
        contact.setPhotoUrl("/users/images/photo.png");
        contact.setVersion(0L);
        return contact;
    }

    public static Session newSession(User user, String refreshToken, LocalDateTime expirationTime) {
        Session session = new Session();
        session.setId(SESSION_ID);
        session.setUserId(user);
        session.setCreateTime(LocalDateTime.now());
        session.setExpirationTime(expirationTime);
        session.setRefreshToken(refreshToken);
        session.setVersion(0L);
        return session;
    }

    public static UserRegisterDto userRegisterDto() {
        return new UserRegisterDto(
                FIRST_NAME_IVAN,
                LAST_NAME_IVANOV,
                MIDDLE_NAME,
                EMAIL_IVANOV,
                PHONE,
                PASSWORD_RAW,
                ROOM_NUMBER_101
        );
    }

    public static SessionLoginDto sessionLoginDto() {
        return new SessionLoginDto(EMAIL_IVANOV, PASSWORD_RAW);
    }

    public static RoleSetDto roleSetDto(UUID userId, Roles role) {
        return new RoleSetDto(userId, role);
    }

    public static RoleEditDto roleEditDto(UUID roleId, Roles role) {
        return new RoleEditDto(roleId, role);
    }

    public static AddLinkRequestDto addLinkRequestDto(LinkType linkType, String link, UUID userId) {
        return new AddLinkRequestDto(linkType, link, userId);
    }

    public static ContactAddRequestDto contactAddRequestDto() {
        return new ContactAddRequestDto(
                LAST_NAME_IVANOV + " " + FIRST_NAME_IVAN + " " + MIDDLE_NAME,
                Roles.STUDENT.toString(),
                EMAIL_IVANOV,
                TG_LINK_WITH_AT,
                VK_LINK_FULL
        );
    }
}
