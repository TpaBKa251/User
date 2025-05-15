package ru.tpu.hostel.user.mapper;

import lombok.experimental.UtilityClass;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.entity.Role;

@UtilityClass
public class RoleMapper {

    public static RoleResponseDto mapRoleToRoleResponseDto(Role role) {
        return new RoleResponseDto(
                role.getId(),
                role.getUser().getId(),
                role.getRole().getRoleName()
        );
    }
}
