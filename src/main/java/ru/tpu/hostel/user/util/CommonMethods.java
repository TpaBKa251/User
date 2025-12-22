package ru.tpu.hostel.user.util;

import lombok.experimental.UtilityClass;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;

@UtilityClass
public class CommonMethods {
    public static String getFullName(Contact contact) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(contact.getLastName()).append(" ").append(contact.getFirstName());

        if (contact.getMiddleName() != null) {
            stringBuilder.append(" ").append(contact.getMiddleName());
        }

        return stringBuilder.toString();
    }

    public static String getFullName(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(user.getLastName()).append(" ").append(user.getFirstName());

        if (user.getMiddleName() != null) {
            stringBuilder.append(" ").append(user.getMiddleName());
        }

        return stringBuilder.toString();
    }
}
