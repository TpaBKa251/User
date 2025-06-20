package ru.tpu.hostel.user.service;

import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleResponseDto setRole(RoleSetDto roleSetDto);

    RoleResponseDto editRole(RoleEditDto roleEditDto);

    RoleResponseDto getRole(UUID roleId);

    List<RoleResponseDto> getUserRoles(UUID userId);

    List<String> getAllUserRoles(UUID userId);

    List<RoleResponseDto> getUsersWithRole(Roles role);

    void deleteRole(UUID userId, Roles role);
}
