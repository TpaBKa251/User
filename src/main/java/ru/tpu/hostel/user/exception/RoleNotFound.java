package ru.tpu.hostel.user.exception;

public class RoleNotFound extends RuntimeException {
    public RoleNotFound(String message) {
        super(message);
    }

    public RoleNotFound() {
        super("Роль не найдена");
    }
}
