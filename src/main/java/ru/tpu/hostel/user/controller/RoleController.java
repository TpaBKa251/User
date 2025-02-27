package ru.tpu.hostel.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.enums.Roles;
import ru.tpu.hostel.user.service.RoleService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/{userId}/{userRoles}")
    public RoleResponseDto setRoleToUser(
            @RequestBody @Valid RoleSetDto roleSetDto,
            @PathVariable Roles[] userRoles,
            @PathVariable UUID userId
    ) {
        List<Roles> role = Arrays.stream(userRoles).toList();
        log.info(role.toString());
        return roleService.setRole(roleSetDto, role, userId);
    }

    @PatchMapping("/edit")
    public RoleResponseDto editRole(@RequestBody @Valid RoleEditDto roleEditDto) {
        return roleService.editRole(roleEditDto);
    }

    @GetMapping("/get/{id}")
    public RoleResponseDto getRole(@PathVariable UUID id) {
        return roleService.getRole(id);
    }

    @GetMapping("/get/user/roles/{userId}")
    public List<RoleResponseDto> getRoleByUserId(@PathVariable UUID userId) {
        return roleService.getUserRoles(userId);
    }

    @GetMapping("/get/user/roles/all/{userId}")
    public List<String> getAllRolesByUserId(@PathVariable UUID userId) {
        return roleService.getAllUserRoles(userId);
    }

    @GetMapping("/get/roles/{role}")
    public List<RoleResponseDto> getRolesByRole(@PathVariable Roles role) {
        return roleService.getUsersWithRole(role);
    }

    @PostMapping("/delete/{userId}/{userRoles}")
    public ResponseEntity<?> deleteRole(
            @RequestBody @Valid RoleSetDto roleSetDto,
            @PathVariable Roles[] userRoles,
            @PathVariable UUID userId
    ) {
        List<Roles> role = Arrays.stream(userRoles).toList();
        return roleService.deleteRole(roleSetDto, role, userId);
    }
}
