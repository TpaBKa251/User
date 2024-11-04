package ru.tpu.hostel.user.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;

@Component
public class RoleMapper {

    public static RoleResponseDto mapRoleToRoleResponseDto(Role role) {
        return new RoleResponseDto(
                role.getId(),
                role.getUser().getId(),
                role.getRole().getRoleName()
        );
    }
}
