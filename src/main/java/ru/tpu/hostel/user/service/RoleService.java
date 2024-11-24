package ru.tpu.hostel.user.service;

import org.springframework.http.ResponseEntity;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.enums.Roles;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleResponseDto setRole(RoleSetDto roleSetDto);

    RoleResponseDto editRole(RoleEditDto roleEditDto);

    RoleResponseDto getRole(UUID roleId);

    List<RoleResponseDto> getUserRoles(UUID userId);

    List<RoleResponseDto> getUsersWithRole(Roles role);

    ResponseEntity<?> deleteRole(UUID roleId);
}
