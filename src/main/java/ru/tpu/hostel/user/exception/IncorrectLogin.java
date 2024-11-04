package ru.tpu.hostel.user.exception;

public class IncorrectLogin extends RuntimeException {
    public IncorrectLogin(String message) {
        super(message);
    }
}
