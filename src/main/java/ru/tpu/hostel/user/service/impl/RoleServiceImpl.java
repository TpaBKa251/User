package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.enums.Roles;
import ru.tpu.hostel.user.exception.RoleNotFound;
import ru.tpu.hostel.user.exception.UserNotFound;
import ru.tpu.hostel.user.mapper.RoleMapper;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.RoleService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public RoleResponseDto setRole(RoleSetDto roleSetDto) {
        Role role = new Role();

        User user = userRepository.findById(roleSetDto.user()).orElseThrow(
                () -> new UserNotFound("Пользователь не найден")
        );

        role.setUser(user);
        role.setRole(roleSetDto.role());

        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Override
    public RoleResponseDto editRole(RoleEditDto roleEditDto) {
        Role role = roleRepository.findById(roleEditDto.id()).orElseThrow(
                () -> new RoleNotFound("Роль не найдена")
        );

        role.setRole(roleEditDto.role());
        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Override
    public RoleResponseDto getRole(UUID roleId) {
        return RoleMapper.mapRoleToRoleResponseDto(roleRepository.findById(roleId).orElseThrow(
                () -> new RoleNotFound("Роль не найдена")
        ));
    }

    @Override
    public List<RoleResponseDto> getUserRoles(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFound("Пользователь не найден")
        );

        List<Role> roles = roleRepository.findByUser(user);

        return roles
                .stream()
                .map(RoleMapper::mapRoleToRoleResponseDto)
                .toList();
    }

    @Override
    public List<RoleResponseDto> getUsersWithRole(Roles role) {
        return roleRepository.findByRole(role)
                .stream()
                .map(RoleMapper::mapRoleToRoleResponseDto)
                .toList();
    }

    @Override
    public ResponseEntity<?> deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new RoleNotFound("Роль не найдена")
        );

        roleRepository.delete(role);

        return ResponseEntity.ok().build();
    }
}
