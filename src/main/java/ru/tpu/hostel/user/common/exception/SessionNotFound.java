package ru.tpu.hostel.user.common.exception;

public class SessionNotFound extends NotFoundException {

    public SessionNotFound(String message) {
        super(message);
    }

    public SessionNotFound() {
        super("Сессия не найдена");
    }
}
