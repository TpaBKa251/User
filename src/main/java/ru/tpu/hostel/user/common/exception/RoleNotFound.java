package ru.tpu.hostel.user.common.exception;

public class RoleNotFound extends NotFoundException {

    public RoleNotFound(String message) {
        super(message);
    }

    public RoleNotFound() {
        super("Роль не найдена");
    }
}
