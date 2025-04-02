package ru.tpu.hostel.user.common.exception;

public class UserNotFound extends NotFoundException {

    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound() {
        super("Пользователь не найден");
    }
}
