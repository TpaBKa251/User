package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationRequestDto;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationType;
import ru.tpu.hostel.internal.service.NotificationSender;
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

    private static final String ROLE_MANAGEMENT_EXCEPTION_MESSAGE = "У вас нет прав для управления этой должностью";

    private static final String ROLE_NOT_FOUND_EXCEPTION_MESSAGE = "Должность не найдена";

    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "Пользователь не найден";

    private static final String USER_ALREADY_HAS_ROLE_EXCEPTION_MESSAGE = "Пользователь уже назначен на эту должность";

    private static final String USER_DOES_NOT_HAVE_ROLE_EXCEPTION_MESSAGE = "Пользователь не назначен на эту должность";

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final NotificationSender notificationSender;

    @Transactional
    @Override
    public RoleResponseDto setRole(RoleSetDto roleSetDto) {
        ExecutionContext context = ExecutionContext.get();
        if (!Roles.hasPermissionToManageRole(context.getUserRoles(), roleSetDto.role())) {
            throw new ServiceException.Forbidden(ROLE_MANAGEMENT_EXCEPTION_MESSAGE);
        }

        if (context.getUserRoles().contains(roleSetDto.role())) {
            log.info("Передаю должность");
            User curUser = userRepository.findById(context.getUserID())
                    .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));
            Role curRole = roleRepository.findByUser(curUser)
                    .stream()
                    .filter(r -> r.getRole().equals(roleSetDto.role()))
                    .findFirst().orElseThrow(() -> new ServiceException.NotFound(ROLE_NOT_FOUND_EXCEPTION_MESSAGE));

            log.info("{}", curRole.getId());

            if (!curUser.getRoles().removeIf(r -> r.getId().equals(curRole.getId()))) {
                throw new ServiceException.InsufficientStorage("Не удалось передать должность. Попробуйте позже");
            }

            roleRepository.deleteById(curRole.getId());
        }

        User user = userRepository.findById(roleSetDto.user())
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));

        Role role = new Role();
        role.setRole(roleSetDto.role());
        role.setUser(user);
        try {
            roleRepository.save(role);
            roleRepository.flush();

            NotificationRequestDto notification = new NotificationRequestDto(
                    roleSetDto.user(),
                    NotificationType.ROLE,
                    "Вас назначили на должность",
                    "Вас назначили на должность " + roleSetDto.role().getRoleName()
            );
            notificationSender.sendNotification(notification);

            return RoleMapper.mapRoleToRoleResponseDto(role);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException.Conflict(USER_ALREADY_HAS_ROLE_EXCEPTION_MESSAGE);
        }


    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public RoleResponseDto editRole(RoleEditDto roleEditDto) {
        if (!Roles.hasPermissionToManageRole(ExecutionContext.get().getUserRoles(), roleEditDto.role())) {
            throw new ServiceException.Forbidden(ROLE_MANAGEMENT_EXCEPTION_MESSAGE);
        }

        Role role = roleRepository.findByIdOptimistic(roleEditDto.id())
                .orElseThrow(() -> new ServiceException.NotFound(ROLE_NOT_FOUND_EXCEPTION_MESSAGE));
        Roles oldRole = role.getRole();
        role.setRole(roleEditDto.role());

        try {
            roleRepository.save(role);
            roleRepository.flush();

            NotificationRequestDto notification = new NotificationRequestDto(
                    role.getUser().getId(),
                    NotificationType.ROLE,
                    "Вас перевели на должность",
                    "Вас перевели с %s на %s".formatted(oldRole.getRoleName(), roleEditDto.role().getRoleName())
            );
            notificationSender.sendNotification(notification);

            return RoleMapper.mapRoleToRoleResponseDto(role);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException.Conflict(USER_ALREADY_HAS_ROLE_EXCEPTION_MESSAGE);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ServiceException.Conflict("Кто-то уже изменил должность. Обновите данные и повторите попытку");
        }
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));

        List<Roles> roles = roleRepository.findByUser(user).stream().map(Role::getRole).toList();

        return Roles.getAllInheritedRoles(roles).stream().map(Roles::toString).toList();
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
    public void deleteRole(UUID userId, Roles role) {
        if (!Roles.hasPermissionToManageRole(ExecutionContext.get().getUserRoles(), role)) {
            throw new ServiceException.Forbidden(ROLE_MANAGEMENT_EXCEPTION_MESSAGE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException.NotFound(USER_NOT_FOUND_EXCEPTION_MESSAGE));

        if (!user.getRoles().stream().map(Role::getRole).toList().contains(role)) {
            throw new ServiceException.Conflict(USER_DOES_NOT_HAVE_ROLE_EXCEPTION_MESSAGE);
        }

        if (!user.getRoles().removeIf(r -> r.getRole().equals(role))) {
            throw new ServiceException.InsufficientStorage("Не удалось снять пользователя с должности. Попробуйте позже.");
        }

        roleRepository.deleteByUserAndRole(user, role);

        NotificationRequestDto notification = new NotificationRequestDto(
                userId,
                NotificationType.ROLE,
                "Вас сняли с должности",
                "Вас сняли с должности " + role.getRoleName()
        );
        notificationSender.sendNotification(notification);
    }

}
