package ru.tpu.hostel.user.common.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }

    public AccessException() {
        super("Доступ запрещен");
    }
}
