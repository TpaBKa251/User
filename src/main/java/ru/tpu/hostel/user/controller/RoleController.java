package ru.tpu.hostel.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.service.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoleResponseDto setRoleToUser(
            @RequestBody @Valid RoleSetDto roleSetDto
    ) {
        return roleService.setRole(roleSetDto);
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
    public List<String> getAllRolesByUserId(@PathVariable UUID userId, HttpServletRequest req) {
        return roleService.getAllUserRoles(userId);
    }

    @GetMapping("/get/roles/{role}")
    public List<RoleResponseDto> getRolesByRole(@PathVariable Roles role) {
        return roleService.getUsersWithRole(role);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteRole(@PathVariable UUID userId, @RequestParam(name = "role") Roles role) {
        roleService.deleteRole(userId, role);
    }
}
