package ru.tpu.hostel.user.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }

    public AccessException() {
        super("Доступ запрещен");
    }
}
