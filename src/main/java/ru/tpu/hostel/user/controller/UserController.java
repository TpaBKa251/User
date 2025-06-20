package ru.tpu.hostel.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserNameResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponseDto registerUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.registerUser(userRegisterDto);
    }

    @GetMapping("/profile")
    public UserResponseDto getUser() {
        return userService.getUser();
    }

    @GetMapping("/get/by/id/{id}")
    public UserShortResponseDto getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/get/by/id/short/{id}")
    public UserShortResponseDto2 getUserByIdShort(@PathVariable UUID id) {
        return userService.getUserByIdShort(id);
    }

    @GetMapping("/get/with/roles/{id}")
    public UserResponseWithRoleDto getUserWithRoles(@PathVariable UUID id) {
        return userService.getUserWithRole(id);
    }

    @GetMapping("/get/all")
    public List<UserResponseDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "1000000000") int size,
            @RequestParam(required = false, defaultValue = "") String firstName,
            @RequestParam(required = false, defaultValue = "") String lastName,
            @RequestParam(required = false, defaultValue = "") String middleName,
            @RequestParam(required = false, defaultValue = "") String room
    ) {
        return userService.getAllUsers(
                page,
                size,
                firstName,
                lastName,
                middleName,
                room
        );
    }

    @GetMapping("/get")
    public List<String> getAllUsersWithName(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String middleName
    ) {
        return userService.getNamesLike(firstName, lastName, middleName);
    }

    @GetMapping("/get/all/by/ids")
    public List<UserResponseDto> getAllUsersWithIds(@RequestParam UUID[] ids) {
        return userService.getAllUsersByIds(Arrays.stream(ids).toList());
    }

    @GetMapping("/get/all/by/ids/short")
    public List<UserShortResponseDto2> getAllUsersWithIdsShort(@RequestParam UUID[] ids) {
        return userService.getAllUsersByIdsShort(Arrays.stream(ids).toList());
    }

    @GetMapping("/get/by/name")
    public List<UserShortResponseDto2> getUsersByName(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam int page,
            @RequestParam int size

    ) {
        return userService.getUserByName(name, page, size);
    }

    @GetMapping("/get/by/role")
    public List<UserShortResponseDto2> getUsersByRole(
            @RequestParam Roles role,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(name = "onMyFloor", required = false, defaultValue = "false") boolean onMyFloor
    ) {
        return userService.getAllUsersByRole(role, page, size, onMyFloor);
    }

    @GetMapping("/get/by/name/with/role")
    public List<UserShortResponseDto2> getUsersByNameWithRole(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam Roles role,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return userService.getUserByNameWithRole(name, role, page, size);
    }

    @GetMapping("/get/by/name/without/role")
    public List<UserShortResponseDto2> getUsersByNameWithoutRole(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam Roles role,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return userService.getUserByNameWithoutRole(name, role, page, size);
    }

    @GetMapping("/get/all/on/floor/{userId}")
    public List<UserNameResponseDto> getAllOnFloor(
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return userService.getAllUsersOnFloor(userId, page, size);
    }

    @GetMapping("/get/room/{userId}")
    public String getRoomNumber(@PathVariable UUID userId) {
        return userService.getRoomNumberByUserId(userId);
    }

    @GetMapping("/get/all/in/rooms")
    public List<UserNameResponseDto> getAllInRooms(@RequestParam String[] roomNumbers) {
        return userService.getAllUsersInRooms(Arrays.stream(roomNumbers).toList());
    }

    @GetMapping("/get/all/in/rooms/with/id")
    List<UUID> getAllInRoomsWithId(@RequestParam String[] roomNumbers) {
        return userService.getAllIdsOfUsersInRooms(Arrays.stream(roomNumbers).toList());
    }

}
