package ru.tpu.hostel.user;

import ru.tpu.hostel.user.entity.User;

public class Data {

    public static final String FIRST_NAME_IVAN = "Иван";

    public static final String FIRST_NAME_BOGDAN = "Богдан";

    public static final String FIRST_NAME_VASILIY = "Василий";

    public static final String FIRST_NAME_LEV = "Лев";

    public static final String FIRST_NAME_LEONID = "Леонид";

    public static final String LAST_NAME_LEVY = "Левый";

    public static final String LAST_NAME_LEONIDOV = "Леонидов";

    public static final String LAST_NAME_IVANOV = "Иванов";

    public static final String LAST_NAME_BOGDANOV = "Богданов";

    public static final String LAST_NAME_VASILYEV = "Васильев";

    public static final String EMAIL_LEVY = "levy@example.com";

    public static final String EMAIL_LEONIDOV = "leonidov@example.com";

    public static final String EMAIL_IVANOV = "ivanov@example.com";

    public static final String EMAIL_BOGDANOV = "bogdanov@example.com";

    public static final String EMAIL_VASILYEV = "vasilyev@example.com";

    public static final String PASSWORD_LEVY = "password789";

    public static final String PASSWORD_LEONIDOV = "password001";

    public static final String PASSWORD_IVANOV = "password123";

    public static final String PASSWORD_BOGDANOV = "password456";

    public static final String PASSWORD_VASILYEV = "password000";

    public static final String ROOM_NUMBER_101 = "101";

    public static final String ROOM_NUMBER_102 = "102";

    public static final String ROOM_NUMBER_104 = "104";

    public static final String ROOM_NUMBER_103 = "103";

    public static final String ROOM_NUMBER_105 = "105";

    public static User getNewUser(
            String firstName,
            String lastName,
            String email,
            String password,
            String roomNumber
    ) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoomNumber(roomNumber);

        return user;
    }
}
