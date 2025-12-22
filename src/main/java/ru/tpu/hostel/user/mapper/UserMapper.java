package ru.tpu.hostel.user.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserNameResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.entity.Contact;
import ru.tpu.hostel.user.entity.User;

@UtilityClass
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

    public static UserResponseDto mapUserToUserResponseDto(User user, Contact contact) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber(),
                contact.getTgLink(),
                contact.getVkLink()
        );
    }

    public static UserResponseDto mapUserToUserResponseWithNullLinksDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber(),
                null,
                null
        );
    }

    public static UserShortResponseDto mapUserToUserShortResponseDto(User user, Contact contact) {
        return new UserShortResponseDto(
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber(),
                contact.getTgLink(),
                contact.getVkLink()
        );
    }

    public static UserShortResponseDto2 mapUserToUserShortResponseDto2(User user, Contact contact) {
        return new UserShortResponseDto2(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                contact.getTgLink(),
                contact.getVkLink()
        );
    }

    public static UserResponseWithRoleDto mapUserToUserResponseWithRoleDto(User user, Contact contact) {
        return new UserResponseWithRoleDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoomNumber(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getRole().getRoleName())
                        .toList(),
                contact.getTgLink(),
                contact.getVkLink()
        );
    }

    public static UserNameResponseDto mapUserToUserNameResponseDto(User user, Contact contact) {
        return new UserNameResponseDto(
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getRoomNumber(),
                contact.getTgLink(),
                contact.getVkLink()
        );
    }
}
