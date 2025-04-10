package ru.tpu.hostel.user.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tpu.hostel.user.common.exception.AccessException;
import ru.tpu.hostel.user.common.exception.IncorrectLogin;
import ru.tpu.hostel.user.common.exception.ManageRoleException;
import ru.tpu.hostel.user.common.exception.NotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(NotFoundException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.NOT_FOUND.value()));
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectLogin.class)
    public ResponseEntity<Map<String, String>> handleIncorrectLogin(IncorrectLogin ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessException.class)
    public ResponseEntity<Map<String, String>> handleAccessException(AccessException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.FORBIDDEN.value()));
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.CONFLICT.value()));
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.CONFLICT.value()));
        map.put("message", ex.getConstraintViolations().toString());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        map.put("message", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ManageRoleException.class)
    public ResponseEntity<Map<String, String>> handleManageRoleException(ManageRoleException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.INSUFFICIENT_STORAGE.value()));
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.INSUFFICIENT_STORAGE);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestCookieException(MissingRequestCookieException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.NOT_FOUND.value()));
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        map.put("message", ex.getMessage());
        map.put("stackTrace", Arrays.toString(ex.getStackTrace()));

        log.error("Ошибка: {}", map, ex);

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
