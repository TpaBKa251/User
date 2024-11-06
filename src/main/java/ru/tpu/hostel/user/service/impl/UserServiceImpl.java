package ru.tpu.hostel.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;
import ru.tpu.hostel.user.enums.Roles;
import ru.tpu.hostel.user.exception.UserNotFound;
import ru.tpu.hostel.user.mapper.UserMapper;
import ru.tpu.hostel.user.repository.RoleRepository;
import ru.tpu.hostel.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserResponseDto registerUser(UserRegisterDto userRegisterDto) {
        User user = UserMapper.mapRegisterDtoToUser(userRegisterDto);

        Role role = new Role();
        role.setUser(user);
        role.setRole(Roles.STUDENT);

        userRepository.save(user);
        roleRepository.save(role);

        return UserMapper.mapUserToUserResponseDto(user);
    }

    public UserResponseDto getUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        return UserMapper.mapUserToUserResponseDto(user);
    }

    public UserShortResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        return UserMapper.mapUserToUserShortResponseDto(user);
    }

    public UserResponseWithRoleDto getUserWithRole(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("Пользователь не найден"));

        return UserMapper.mapUserToUserResponseWithRoleDto(user);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new UserNotFound("Пользователи не найден");
        }

        return users.stream().map(UserMapper::mapUserToUserResponseDto).toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
