package ru.tpu.hostel.user.exception;

public class UserNotFound extends NotFoundException {

    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound() {
        super("Пользователь не найден");
    }
}
