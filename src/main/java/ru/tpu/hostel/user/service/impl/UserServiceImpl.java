package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.internal.common.logging.LogFilter;
import ru.tpu.hostel.internal.common.logging.SecretArgument;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.client.ClientAdminService;
import ru.tpu.hostel.user.dto.request.BalanceRequestDto;
import ru.tpu.hostel.user.dto.request.DocumentRequestDto;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserNameResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.mapper.UserMapper;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ClientAdminService adminService;

    @Transactional
    @LogFilter(enableParamsLogging = false, enableResultLogging = false)
    public UserResponseDto registerUser(@SecretArgument UserRegisterDto userRegisterDto) {
        User user = UserMapper.mapRegisterDtoToUser(userRegisterDto);

        Role role = new Role();
        role.setUser(user);
        role.setRole(Roles.STUDENT);

        user = userRepository.save(user);
        roleRepository.save(role);

        adminService.addBalance(new BalanceRequestDto(user.getId(), BigDecimal.ZERO));
        adminService.addDocument(new DocumentRequestDto(
                user.getId(),
                "CERTIFICATE",
                LocalDate.of(0, 1, 1),
                LocalDate.now()
        ));
        adminService.addDocument(new DocumentRequestDto(
                user.getId(),
                "FLUOROGRAPHY",
                LocalDate.of(0, 1, 1),
                LocalDate.now()
        ));

        return UserMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto getUser() {
        User user = userRepository.findById(ExecutionContext.get().getUserID()).orElseThrow(ServiceException.NotFound::new);

        return UserMapper.mapUserToUserResponseDto(user);
    }

    public UserShortResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(ServiceException.NotFound::new);

        return UserMapper.mapUserToUserShortResponseDto(user);
    }

    public UserResponseWithRoleDto getUserWithRole(UUID id) {
        User user = userRepository.findById(id).orElseThrow(ServiceException.NotFound::new);

        return UserMapper.mapUserToUserResponseWithRoleDto(user);
    }

    public List<UserResponseDto> getAllUsers(
            int page,
            int size,
            String firstName,
            String lastName,
            String middleName,
            String room
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<User> users = userRepository.findAllByFilter(firstName, lastName, middleName, room, pageable);

        if (users.isEmpty()) {
            throw new ServiceException.NotFound();
        }

        return users.stream()
                .map(UserMapper::mapUserToUserResponseDto)
                .toList();
    }

    public List<UserShortResponseDto2> getUserByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        return userRepository.findAllByFullName(name == null ? "" : name, pageable).stream()
                .map(UserMapper::mapUserToUserShortResponseDto2)
                .toList();
    }

    public List<String> getNamesLike(String firstName, String lastName, String middleName) {
        if (firstName != null) {
            return userRepository.findDistinctByFirstNameLikeIgnoreCase(firstName);
        } else if (lastName != null) {
            return userRepository.findDistinctByLastNameLikeIgnoreCase(lastName);
        } else if (middleName != null) {
            return userRepository.findDistinctByMiddleNameLikeIgnoreCase(middleName);
        }

        return List.of();
    }

    public List<UserResponseDto> getAllUsersByIds(List<UUID> ids) {
        return userRepository.findByIdInOrderById(ids)
                .stream()
                .map(UserMapper::mapUserToUserResponseDto)
                .toList();
    }

    @LogFilter(enableMethodLogging = false)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public List<UserShortResponseDto2> getUserByNameWithRole(String name, Roles role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        return userRepository.findAllByFullNameWithRole(name == null ? "" : name, role, pageable)
                .stream()
                .map(UserMapper::mapUserToUserShortResponseDto2)
                .toList();
    }

    public List<UserShortResponseDto2> getAllUsersByRole(Roles role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        return userRepository.findAllByRoles_Role(role, pageable)
                .stream()
                .map(UserMapper::mapUserToUserShortResponseDto2)
                .toList();
    }

    public List<UserShortResponseDto2> getUserByNameWithoutRole(String name, Roles role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        return userRepository.findAllByFullNameWithoutRole(name == null ? "" : name, role, pageable)
                .stream()
                .map(UserMapper::mapUserToUserShortResponseDto2)
                .toList();
    }

    public List<UserShortResponseDto2> getAllUsersByIdsShort(List<UUID> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(UserMapper::mapUserToUserShortResponseDto2)
                .toList();
    }

    public UserShortResponseDto2 getUserByIdShort(UUID id) {
        return UserMapper.mapUserToUserShortResponseDto2(userRepository.findById(id).orElseThrow(ServiceException.NotFound::new));
    }

    public List<UserNameResponseDto> getAllUsersOnFloor(UUID userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(ServiceException.NotFound::new);
        String roomNumber = user.getRoomNumber();

        Pageable pageable = PageRequest.of(page, size, Sort.by("roomNumber"));

        return userRepository
                .findAllByRoomNumberStartingWithOrderByRoomNumber(String.valueOf(roomNumber.charAt(0)), pageable)
                .stream()
                .map(UserMapper::mapUserToUserNameResponseDto)
                .toList();
    }

    public String getRoomNumberByUserId(UUID userId) {
        return userRepository.findRoomNumberById(userId).orElseThrow(ServiceException.NotFound::new);
    }

    public List<UserNameResponseDto> getAllUsersInRooms(List<String> roomNumbers) {
        return userRepository.findAllByRoomNumberInOrderByRoomNumber(roomNumbers).stream()
                .map(UserMapper::mapUserToUserNameResponseDto)
                .toList();
    }

    public List<UUID> getAllIdsOfUsersInRooms(List<String> roomNumbers) {
        return userRepository.findAllIdsOfUsersInRooms(roomNumbers);
    }
}
