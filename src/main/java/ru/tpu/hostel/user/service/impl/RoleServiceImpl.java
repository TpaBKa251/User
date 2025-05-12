package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.mapper.RoleMapper;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;
import ru.tpu.hostel.user.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public RoleResponseDto setRole(RoleSetDto roleSetDto) {
        ExecutionContext context = ExecutionContext.get();
        if (!Roles.hasPermissionToManageRole(context.getUserRoles(), roleSetDto.role())) {
            throw new ServiceException.Forbidden("У вас нет прав для управления этой ролью");
        }

        if (context.getUserRoles().contains(roleSetDto.role())) {
            log.info("Передаю роль");
            User curUser = userRepository.findById(context.getUserID()).orElseThrow(ServiceException.NotFound::new);
            Role curRole = roleRepository.findByUser(curUser)
                    .stream()
                    .filter(r -> r.getRole().equals(roleSetDto.role()))
                    .findFirst().orElseThrow(ServiceException.NotFound::new);

            log.info("{}", curRole.getId());

            if (!curUser.getRoles().removeIf(r -> r.getId().equals(curRole.getId()))) {
                throw new ServiceException.InsufficientStorage("Не удалось передать роль");
            }

            roleRepository.deleteById(curRole.getId());
        }

        User user = userRepository.findById(roleSetDto.user()).orElseThrow(ServiceException.NotFound::new);

        Role role = new Role();
        role.setRole(roleSetDto.role());
        role.setUser(user);

        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Transactional
    @Override
    public RoleResponseDto editRole(RoleEditDto roleEditDto) {
        Role role = roleRepository.findById(roleEditDto.id()).orElseThrow(ServiceException.NotFound::new);

        role.setRole(roleEditDto.role());
        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Override
    public RoleResponseDto getRole(UUID roleId) {
        return RoleMapper.mapRoleToRoleResponseDto(roleRepository.findById(roleId).orElseThrow(ServiceException.NotFound::new));
    }

    @Override
    public List<RoleResponseDto> getUserRoles(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(ServiceException.NotFound::new);

        List<Role> roles = roleRepository.findByUser(user);

        return roles
                .stream()
                .map(RoleMapper::mapRoleToRoleResponseDto)
                .toList();
    }

    @Override
    public List<String> getAllUserRoles(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(ServiceException.NotFound::new);

        List<Role> roles = roleRepository.findByUser(user);

        Set<Roles> allRolesDistinct = new HashSet<>();
        for (Role role : roles) {
            allRolesDistinct.add(role.getRole());
        }

        return Roles.getAllInheritedRoles(allRolesDistinct).stream().map(Roles::toString).toList();
    }

    @Override
    public List<RoleResponseDto> getUsersWithRole(Roles role) {
        return roleRepository.findByRole(role)
                .stream()
                .map(RoleMapper::mapRoleToRoleResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteRole(RoleSetDto roleSetDto, UUID userId) {
        if (!Roles.hasPermissionToManageRole(ExecutionContext.get().getUserRoles(), roleSetDto.role())) {
            throw new ServiceException.Forbidden("У вас нет прав для управления этой ролью");
        }

        User user = userRepository.findById(roleSetDto.user()).orElseThrow(ServiceException.NotFound::new);

        if (!user.getRoles().removeIf(r -> r.getRole().equals(roleSetDto.role()))) {
            throw new ServiceException.InsufficientStorage("Не удалось снять пользователя с роли");
        }

        roleRepository.deleteByUserAndRole(user, roleSetDto.role());

        return ResponseEntity.ok().build();
    }

}
