package ru.tpu.hostel.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    public UserResponseDto registerUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.registerUser(userRegisterDto);
    }

    @GetMapping("/profile/{id}")
    public UserResponseDto getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }

    @GetMapping("/get/by/id/{id}")
    public UserShortResponseDto getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/get/with/roles/{id}")
    public UserResponseWithRoleDto getUserWithRoles(@PathVariable UUID id) {
        return userService.getUserWithRole(id);
    }

    @GetMapping("/get/all")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
