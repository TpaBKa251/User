package ru.tpu.hostel.user.service;

import org.springframework.http.ResponseEntity;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.enums.Roles;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleResponseDto setRole(RoleSetDto roleSetDto, List<Roles> role, UUID userId);

    RoleResponseDto editRole(RoleEditDto roleEditDto);

    RoleResponseDto getRole(UUID roleId);

    List<RoleResponseDto> getUserRoles(UUID userId);

    List<String> getAllUserRoles(UUID userId);

    List<RoleResponseDto> getUsersWithRole(Roles role);

    ResponseEntity<?> deleteRole(RoleSetDto roleSetDto, List<Roles> role, UUID userId);
}
