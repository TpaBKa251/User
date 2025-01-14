package ru.tpu.hostel.user.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.ActiveEventDto;
import ru.tpu.hostel.user.dto.response.AdminUserResponse;
import ru.tpu.hostel.user.dto.response.CertificateDto;
import ru.tpu.hostel.user.dto.response.SuperUserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.entity.User;

import java.math.BigDecimal;
import java.util.List;

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

    public static UserShortResponseDto2 mapUserToUserShortResponseDto2(User user) {
        return new UserShortResponseDto2(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName()
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
                user.getRoles()
                        .stream()
                        .map(role -> role.getRole().getRoleName())
                        .toList()
        );
    }

    /**
     * @deprecated логика перенесена в API Gateway
     */
    @Deprecated
    public static SuperUserResponseDto SuperMapper(
            User user,
            BigDecimal balance,
            CertificateDto cert1,
            CertificateDto cert2,
            List<ActiveEventDto> books
    ) {
        return new SuperUserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getRoomNumber(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getRole().getRoleName())
                        .toList(),
                balance,
                cert1,
                cert2,
                books
        );
    }

    /**
     * @deprecated логика перенесена в API Gateway
     */
    @Deprecated
    public static AdminUserResponse mapUserToAdminUserResponse(
            User user,
            CertificateDto pediculosis,
            CertificateDto fluorography,
            BigDecimal balance
    ) {
        return new AdminUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getRoomNumber(),
                pediculosis,
                fluorography,
                balance
        );
    }
}
