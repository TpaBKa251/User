package ru.tpu.hostel.user.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.entity.Role;
import ru.tpu.hostel.user.entity.User;

@Component
public class UserMapper {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static User mapRegisterDtoToUser(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setFirstName(userRegisterDto.firstName());
        user.setLastName(userRegisterDto.lastName());
        user.setMiddleName(userRegisterDto.middleName());
        user.setEmail(userRegisterDto.email());
        user.setPhone(userRegisterDto.phoneNumber());
        user.setPassword(passwordEncoder.encode(userRegisterDto.password()));
        user.setRoomNumber(userRegisterDto.roomNumber());

        return user;
    }

    public static UserResponseDto mapUserToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber()
        );
    }

    public static UserShortResponseDto mapUserToUserShortResponseDto(User user) {
        return new UserShortResponseDto(
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber()
        );
    }

    public static UserResponseWithRoleDto mapUserToUserResponseWithRoleDto(User user) {
        return new UserResponseWithRoleDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber(),
                user.getRoles().stream().map(role -> role.getRole().getRoleName()).toList()
        );
    }
}
