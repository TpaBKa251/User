package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String ROLE_MANAGEMENT_EXCEPTION_MESSAGE = "У вас нет прав для управления этой ролью";

    private static final String ROLE_NOT_FOUND_EXCEPTION_MESSAGE = "Роль не найдена";

    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "Пользователь не найден";

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    @Transactional
    @Override
    @Retryable(
            retryFor = ServiceException.InsufficientStorage.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 50, multiplier = 1)
    )
    public RoleResponseDto setRole(RoleSetDto roleSetDto) {
        ExecutionContext context = ExecutionContext.get();
        if (!Roles.hasPermissionToManageRole(context.getUserRoles(), roleSetDto.role())) {
            throw new ServiceException.Forbidden(ROLE_MANAGEMENT_EXCEPTION_MESSAGE);
        }

        if (context.getUserRoles().contains(roleSetDto.role())) {
            log.info("Передаю роль");
            User curUser = userRepository.findById(context.getUserID())
                    .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));
            Role curRole = roleRepository.findByUser(curUser)
                    .stream()
                    .filter(r -> r.getRole().equals(roleSetDto.role()))
                    .findFirst().orElseThrow(() -> new ServiceException.NotFound(ROLE_NOT_FOUND_EXCEPTION_MESSAGE));

            log.info("{}", curRole.getId());

            if (!curUser.getRoles().removeIf(r -> r.getId().equals(curRole.getId()))) {
                throw new ServiceException.InsufficientStorage("Не удалось передать роль. Попробуйте позже");
            }

            roleRepository.deleteById(curRole.getId());
        }

        User user = userRepository.findById(roleSetDto.user())
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));

        Role role = new Role();
        role.setRole(roleSetDto.role());
        role.setUser(user);

        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            backoff = @Backoff(delay = 100, multiplier = 2),
            recover = "recoverEditRole"
    )
    public RoleResponseDto editRole(RoleEditDto roleEditDto) {
        if (!Roles.hasPermissionToManageRole(ExecutionContext.get().getUserRoles(), roleEditDto.role())) {
            throw new ServiceException.Forbidden(ROLE_MANAGEMENT_EXCEPTION_MESSAGE);
        }

        Role role = roleRepository.findByIdOptimistic(roleEditDto.id())
                .orElseThrow(() -> new ServiceException.NotFound(ROLE_NOT_FOUND_EXCEPTION_MESSAGE));

        role.setRole(roleEditDto.role());
        roleRepository.save(role);

        return RoleMapper.mapRoleToRoleResponseDto(role);
    }

    @Recover
    public RoleResponseDto recoverEditRole(ObjectOptimisticLockingFailureException e, RoleEditDto roleEditDto) {
        throw new ServiceException.Conflict("Не удалось отредактировать роль. Попробуйте позже");
    }

    @Override
    public RoleResponseDto getRole(UUID roleId) {
        return RoleMapper.mapRoleToRoleResponseDto(roleRepository.findById(roleId)
                .orElseThrow(() -> new ServiceException.NotFound(ROLE_NOT_FOUND_EXCEPTION_MESSAGE)));
    }

    @Override
    public List<RoleResponseDto> getUserRoles(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));

        List<Role> roles = roleRepository.findByUser(user);

        return roles
                .stream()
                .map(RoleMapper::mapRoleToRoleResponseDto)
                .toList();
    }

    @Override
    public List<String> getAllUserRoles(UUID userId) {
        return Roles.getAllInheritedRoles(ExecutionContext.get().getUserRoles()).stream().map(Roles::toString).toList();
    }

    @Override
    public List<RoleResponseDto> getUsersWithRole(Roles role) {
        return roleRepository.findByRole(role)
                .stream()
                .map(RoleMapper::mapRoleToRoleResponseDto)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    @Retryable(
            retryFor = ServiceException.InsufficientStorage.class,
            maxAttempts = 2,
            backoff = @Backoff(delay = 50, multiplier = 1)
    )
    public ResponseEntity<?> deleteRole(RoleSetDto roleSetDto) {
        if (!Roles.hasPermissionToManageRole(ExecutionContext.get().getUserRoles(), roleSetDto.role())) {
            throw new ServiceException.Forbidden(ROLE_MANAGEMENT_EXCEPTION_MESSAGE);
        }

        User user = userRepository.findById(roleSetDto.user())
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));

        if (!user.getRoles().removeIf(r -> r.getRole().equals(roleSetDto.role()))) {
            throw new ServiceException.InsufficientStorage("Не удалось снять пользователя с роли. Попробуйте позже.");
        }

        roleRepository.deleteByUserAndRole(user, roleSetDto.role());

        return ResponseEntity.ok().build();
    }

}
