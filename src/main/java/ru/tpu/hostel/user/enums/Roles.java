package ru.tpu.hostel.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Роли юзеров
 */
@RequiredArgsConstructor
public enum Roles {

    ADMINISTRATION("Администрация", null, null, null),

    HOSTEL_SUPERVISOR("Староста общежития", ADMINISTRATION, null, null),

    FLOOR_SUPERVISOR("Староста этажа", HOSTEL_SUPERVISOR, null, null),

    RESPONSIBLE_KITCHEN("Ответственный за кухню", FLOOR_SUPERVISOR, ResourceType.KITCHEN, ResourceType.KITCHEN),

    RESPONSIBLE_HALL("Ответственный за зал", HOSTEL_SUPERVISOR, ResourceType.HALL, ResourceType.HALL),

    RESPONSIBLE_GYM("Главный за спортзал", HOSTEL_SUPERVISOR, ResourceType.GYM, ResourceType.GYM),

    WORKER_GYM("Ответственный за спортзал", RESPONSIBLE_GYM, null, ResourceType.GYM),

    RESPONSIBLE_FIRE_SAFETY("Ответственный за пожарную безопасность", HOSTEL_SUPERVISOR, null, null),

    RESPONSIBLE_SANITARY("Ответственный за санитарную проверку", FLOOR_SUPERVISOR, null, null),

    RESPONSIBLE_INTERNET("Ответственный за подключение к Интернету", HOSTEL_SUPERVISOR, ResourceType.INTERNET, ResourceType.INTERNET),

    RESPONSIBLE_SOOP("Ответственный за СООП", HOSTEL_SUPERVISOR, ResourceType.SOOP, ResourceType.SOOP),

    WORKER_FIRE_SAFETY("Работник пожарной безопасности", RESPONSIBLE_FIRE_SAFETY, null, null),

    WORKER_SANITARY("Работник санитарной проверки", RESPONSIBLE_SANITARY, null, null),

    WORKER_SOOP("Работник СООП", RESPONSIBLE_SOOP, null, ResourceType.SOOP),

    STUDENT("Студент", null, null, null);

    /**
     * Маппа с информацией о том, на сколько роль далека от Администрации
     */
    private static final Map<Roles, Integer> ROLE_DEPTH_CACHE = new EnumMap<>(Roles.class);

    static {
        for (Roles role : Roles.values()) {
            int depth = 0;
            Roles current = role;
            while (current != null) {
                depth++;
                current = current.parentRole;
            }
            ROLE_DEPTH_CACHE.put(role, depth);
        }
    }

    /**
     * Имя роли
     */
    @Getter
    private final String roleName;

    /**
     * Родительская роль
     */
    private final Roles parentRole;

    /**
     * Ресурс, которым может управлять роль
     */
    private final ResourceType managingResourceType;

    /**
     * Ресурс, на который может быть назначена роль в качестве ответственного/управляющего
     */
    private final ResourceType assignedResourceType;

    /**
     * Возвращает самую старшую роль из коллекции
     *
     * @param roles коллекция ролей
     * @return старшую роль
     */
    public static Roles getSeniorRole(Collection<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }

        return roles.stream()
                .min(Comparator.comparing(ROLE_DEPTH_CACHE::get))
                .orElse(null);
    }

    /**
     * Возвращает самую старшую роль из массива
     *
     * @param roles массив ролей
     * @return старшую роль
     */
    public static Roles getSeniorRole(Roles[] roles) {
        if (roles == null || roles.length == 0) {
            return null;
        }
        Collection<Roles> rolesCollection = new HashSet<>(Arrays.asList(roles));
        return getSeniorRole(rolesCollection);
    }

    /**
     * Возвращает самую младшую роль из коллекции
     *
     * @param roles коллекция ролей
     * @return младшую роль
     */
    public static Roles getJuniorRole(Collection<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }

        return roles.stream()
                .max(Comparator.comparing(ROLE_DEPTH_CACHE::get))
                .orElse(null);
    }

    /**
     * Возвращает самую младшую роль из массива
     *
     * @param roles массив ролей
     * @return младшую роль
     */
    public static Roles getJuniorRole(Roles[] roles) {
        if (roles == null || roles.length == 0) {
            return null;
        }
        Collection<Roles> rolesCollection = new HashSet<>(Arrays.asList(roles));
        return getJuniorRole(rolesCollection);
    }

    /**
     * Проверят, может ли хоть одна из ролей коллекции быть назначена в качестве ответственного/управляющего ресурсом
     *
     * @param roles              коллекция ролей
     * @param targetResourceType ресурс
     * @return разрешение на назначение
     */
    public static boolean canBeAssignedToResourceType(Collection<Roles> roles, ResourceType targetResourceType) {
        if (roles == null || roles.isEmpty() || targetResourceType == null) {
            return false;
        }

        return roles.stream().anyMatch(role -> role.canBeAssignedToResourceType(targetResourceType));
    }

    /**
     * Проверят, может ли хоть одна из ролей массива быть назначена в качестве ответственного/управляющего ресурсом
     *
     * @param roles              массив ролей
     * @param targetResourceType ресурс
     * @return разрешение на назначение
     */
    public static boolean canBeAssignedToResourceType(Roles[] roles, ResourceType targetResourceType) {
        if (roles == null || roles.length == 0 || targetResourceType == null) {
            return false;
        }
        Collection<Roles> rolesCollection = new HashSet<>(Arrays.asList(roles));
        return canBeAssignedToResourceType(rolesCollection, targetResourceType);
    }

    /**
     * Возвращает все роли, от которых наследуется старшая из коллекции. Идем вниз по иерархии
     *
     * @param roles коллекция ролей
     * @return всех потомков ролей
     */
    public static Set<Roles> getAllInheritedRoles(Collection<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        Roles seniorRole = getSeniorRole(roles);
        if (seniorRole == null) {
            return Collections.emptySet();
        }
        return seniorRole.getAllInheritedRoles();
    }

    /**
     * Возвращает все роли, от которых наследуется старшая из массива. Идем вниз по иерархии
     *
     * @param roles массив ролей
     * @return всех потомков старшей роли
     */
    public static Set<Roles> getAllInheritedRoles(Roles[] roles) {
        if (roles == null || roles.length == 0) {
            return Collections.emptySet();
        }
        Set<Roles> rolesSet = new HashSet<>(Arrays.asList(roles));
        return getAllInheritedRoles(rolesSet);
    }

    /**
     * Возвращает все роли, которым подчиняется старшая из коллекции. Идем вверх по иерархии
     *
     * @param roles коллекция ролей
     * @return всех родителей старшей роли
     */
    public static Set<Roles> getAllParentRoles(Collection<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        Roles seniorRole = getSeniorRole(roles);
        if (seniorRole == null) {
            return Collections.emptySet();
        }
        return seniorRole.getAllParentRoles();
    }

    /**
     * Возвращает все роли, которым подчиняется старшая из массива. Идем вверх по иерархии
     *
     * @param roles массив ролей
     * @return всех родителей старшей роли
     */
    public static Set<Roles> getAllParentRoles(Roles[] roles) {
        if (roles == null || roles.length == 0) {
            return Collections.emptySet();
        }
        Set<Roles> rolesSet = new HashSet<>(Arrays.asList(roles));
        return getAllParentRoles(rolesSet);
    }

    /**
     * Проверяет, может ли хоть одна из ролей коллекции управлять другой (назначать, редактировать, удалять)
     *
     * @param roles      коллекция ролей
     * @param targetRole роль для управления
     * @return разрешение на управление
     */
    public static boolean hasPermissionToManageRole(Collection<Roles> roles, Roles targetRole) {
        if (roles == null || roles.isEmpty() || targetRole == null) {
            return false;
        }

        Roles seniorRole = getSeniorRole(roles);
        if (seniorRole == null) {
            return false;
        }
        return seniorRole.hasPermissionToManageRole(targetRole);
    }

    /**
     * Проверяет, может ли хоть одна из ролей массива управлять другой (назначать, редактировать, удалять)
     *
     * @param roles      массив ролей
     * @param targetRole роль для управления
     * @return разрешение на управление
     */
    public static boolean hasPermissionToManageRole(Roles[] roles, Roles targetRole) {
        if (roles == null || roles.length == 0 || targetRole == null) {
            return false;
        }
        Set<Roles> rolesSet = new HashSet<>(Arrays.asList(roles));
        return hasPermissionToManageRole(rolesSet, targetRole);
    }

    /**
     * Проверяет, может ли хоть одна из ролей коллекции управлять ресурсом (редактировать, удалять)
     *
     * @param roles              коллекция ролей
     * @param targetResourceType ресурс
     * @return разрешение на управление
     */
    public static boolean hasPermissionToManageResourceType(Collection<Roles> roles, ResourceType targetResourceType) {
        if (roles == null || roles.isEmpty() || targetResourceType == null) {
            return false;
        }

        Roles seniorRole = getSeniorRole(roles);
        if (seniorRole == null) {
            return false;
        }
        return seniorRole.hasPermissionToManageResourceType(targetResourceType);
    }

    /**
     * Проверяет, может ли хоть одна из ролей массива управлять ресурсом (редактировать, удалять)
     *
     * @param roles              массив ролей
     * @param targetResourceType ресурс
     * @return разрешение на управление
     */
    public static boolean hasPermissionToManageResourceType(Roles[] roles, ResourceType targetResourceType) {
        if (roles == null || roles.length == 0 || targetResourceType == null) {
            return false;
        }
        Set<Roles> rolesSet = new HashSet<>(Arrays.asList(roles));
        return hasPermissionToManageResourceType(rolesSet, targetResourceType);
    }

    /**
     * Все роли, от которых наследуется текущая. Идем от текущей роли вниз по иерархии
     */
    private Set<Roles> getAllInheritedRoles() {
        if (this == STUDENT) {
            return Collections.emptySet();
        }

        Set<Roles> roles = new HashSet<>();
        roles.add(this);
        for (Roles role : values()) {
            if (role.parentRole == this) {
                roles.addAll(role.getAllInheritedRoles());
            }
        }

        return Collections.unmodifiableSet(roles);
    }

    /**
     * Все роли, которым подчиняется текущая. Идем от текущей роли вверх по иерархии
     */
    private Set<Roles> getAllParentRoles() {
        Set<Roles> roles = new HashSet<>();

        Roles currentRole = this;

        while (currentRole != null) {
            roles.add(currentRole);
            currentRole = currentRole.parentRole;
        }

        return Collections.unmodifiableSet(roles);
    }

    /**
     * Проверяет, может ли текущая роль управлять другой (назначать, редактировать, удалять)
     *
     * @param targetRole роль для управления
     * @return разрешение на управление
     */
    private boolean hasPermissionToManageRole(Roles targetRole) {
        if (targetRole == null) {
            return false;
        }
        return getAllInheritedRoles().contains(targetRole);
    }

    /**
     * Проверяет, может ли текущая роль управлять ресурсом (редактировать, удалять)
     *
     * @param targetResourceType ресурс
     * @return разрешение на управление
     */
    private boolean hasPermissionToManageResourceType(ResourceType targetResourceType) {
        if (targetResourceType == null) {
            return false;
        }
        return getAllInheritedRoles().stream().anyMatch(role -> role.managingResourceType == targetResourceType);
    }

    /**
     * Проверят, может ли текущая роль быть назначена в качестве ответственного/управляющего ресурсом
     *
     * @param targetResourceType ресурс
     * @return разрешение на назначение
     */
    private boolean canBeAssignedToResourceType(ResourceType targetResourceType) {
        if (targetResourceType == null) {
            return false;
        }
        return assignedResourceType == targetResourceType;
    }

}