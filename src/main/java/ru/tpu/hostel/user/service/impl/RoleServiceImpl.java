package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.enums.Roles;
import ru.tpu.hostel.user.exception.AccessException;
import ru.tpu.hostel.user.exception.RoleNotFound;
import ru.tpu.hostel.user.exception.UserNotFound;
import ru.tpu.hostel.user.mapper.RoleMapper;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.RoleService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public RoleResponseDto setRole(RoleSetDto roleSetDto, List<Roles> userRole, UUID userId) {
        boolean canSetRole = false;

        for (Roles role : userRole) {
            log.info("{}", role.getAllInheritedRoles());
            canSetRole = role.hasPermissionToSetRole(roleSetDto.role());
            if (canSetRole) {
                break;
            }
        }

        if (!canSetRole) {
            throw new AccessException("У вас нет прав управлять этой ролью");
        }

        User user = userRepository.findById(roleSetDto.user()).orElseThrow(UserNotFound::new);

        Role role = new Role();
        role.setRole(roleSetDto.role());
        role.setUser(user);

        if (userRole.contains(roleSetDto.role())) {
            log.info("Передаю роль");
            User curUser = userRepository.findById(userId).orElseThrow(UserNotFound::new);
            Role curRole = roleRepository.findByUser(curUser)
                    .stream()
                    .filter(r -> r.getRole().equals(roleSetDto.role()))
                    .findFirst().orElseThrow(RoleNotFound::new);

            log.info("{}", curRole.getId());

            curUser.getRoles().removeIf(r -> r.getId().equals(curRole.getId()));
            roleRepository.deleteById(curRole.getId());
            roleRepository.flush();
        }

        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Override
    public RoleResponseDto editRole(RoleEditDto roleEditDto) {
        Role role = roleRepository.findById(roleEditDto.id()).orElseThrow(RoleNotFound::new);

        role.setRole(roleEditDto.role());
        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Override
    public RoleResponseDto getRole(UUID roleId) {
        return RoleMapper.mapRoleToRoleResponseDto(roleRepository.findById(roleId).orElseThrow(RoleNotFound::new));
    }

    @Override
    public List<RoleResponseDto> getUserRoles(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);

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
        Role role = roleRepository.findById(roleId).orElseThrow(RoleNotFound::new );

        roleRepository.delete(role);

        return ResponseEntity.ok().build();
    }
}
