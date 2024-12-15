package ru.tpu.hostel.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum Roles {
    ADMINISTRATION("Администрация", null),
    HOSTEL_SUPERVISOR("Староста общежития", ADMINISTRATION),
    FLOOR_SUPERVISOR("Староста этажа", HOSTEL_SUPERVISOR),
    RESPONSIBLE_KITCHEN("Ответственный за кухню", FLOOR_SUPERVISOR),
    RESPONSIBLE_HALL("Ответственный за зал", HOSTEL_SUPERVISOR),
    RESPONSIBLE_FIRE_SAFETY("Ответственный за пожарную безопасность", HOSTEL_SUPERVISOR),
    RESPONSIBLE_SANITARY("Ответственный за санитарную проверку", FLOOR_SUPERVISOR),
    RESPONSIBLE_INTERNET("Ответственный за подключение к Интернету", HOSTEL_SUPERVISOR),
    RESPONSIBLE_SOOP("Ответственный за СООП", HOSTEL_SUPERVISOR),
    WORKER_FIRE_SAFETY("Работник пожарной безопасности", RESPONSIBLE_FIRE_SAFETY),
    WORKER_SANITARY("Работник санитарной проверки", RESPONSIBLE_SANITARY),
    WORKER_SOOP("Работник СООП", RESPONSIBLE_SOOP),
    STUDENT("Студент", null),
    ;

    private final String roleName;
    private final Roles parentRole;

    /**
     * Возвращает все роли, которые наследуются от текущей роли (включая саму роль).
     */
    public List<Roles> getAllInheritedRoles() {
        List<Roles> roles = new ArrayList<>();

        if (this == STUDENT) {
            return roles;
        }

        roles.add(this);

        for (Roles role : Roles.values()) {
            if (role.parentRole == this) {
                roles.addAll(role.getAllInheritedRoles());
            }
        }
        return roles;
    }

    /**
     * Проверяет, имеет ли текущая роль права на выполнение действия, которое доступно для другой роли.
     */
    public boolean hasPermissionToSetRole(Roles targetRole) {
        return getAllInheritedRoles().contains(targetRole);
    }
}

