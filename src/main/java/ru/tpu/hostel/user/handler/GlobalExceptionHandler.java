package ru.tpu.hostel.user.handler;

import jakarta.validation.ConstraintViolationException;
import jdk.jshell.spi.ExecutionControl;
import org.apache.coyote.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tpu.hostel.user.exception.AccessException;
import ru.tpu.hostel.user.exception.IncorrectLogin;
import ru.tpu.hostel.user.exception.SessionNotFound;
import ru.tpu.hostel.user.exception.UserNotFound;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFound ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "404");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessionNotFound.class)
    public ResponseEntity<Map<String, String>> handleSessionNotFound(SessionNotFound ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "404");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectLogin.class)
    public ResponseEntity<Map<String, String>> handleIncorrectLogin(IncorrectLogin ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "401");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<Map<String, String>> handleAccessException(AccessException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "403");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "409");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "400");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "500");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
