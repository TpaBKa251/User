package ru.tpu.hostel.user.exception;

public class SessionNotFound extends RuntimeException {
    public SessionNotFound(String message) {
        super(message);
    }

    public SessionNotFound() {
        super("Сессия не найдена");
    }
}
