package ru.tpu.hostel.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.RoleEditDto;
import ru.tpu.hostel.user.dto.request.RoleSetDto;
import ru.tpu.hostel.user.dto.response.RoleResponseDto;
import ru.tpu.hostel.user.service.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Tag(name = "Роли/должности", description = "Эндпоинты для работы с ролями/должностями юзеров")
@ApiResponse(responseCode = "500", description = "Неизвестная ошибка сервера", content = @Content)
@ApiResponse(
        responseCode = "400",
        description = "Неверный запрос от клиента, нарушение ограничений запроса (тело, параметры)",
        content = @Content
)
@ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
public class RoleController {

    private final RoleService roleService;

    @Operation(
            summary = "Создать роль",
            description = "Присваивает юзеру роль. Если у текущего юзера (отправившего запрос) такая же роль, то она "
                    + "передается: у текущего она удаляется, у указанного - появляется",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное создание (присваивание) роли"),
                    @ApiResponse(responseCode = "403", description = "Нет прав управлять ролью", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Не найден юзер или роль", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Юзер уже назначен на роль", content = @Content),
                    @ApiResponse(responseCode = "507", description = "Не удалось передать роль", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoleResponseDto setRoleToUser(@RequestBody @Valid RoleSetDto roleSetDto) {
        return roleService.setRole(roleSetDto);
    }

    @Operation(
            summary = "Изменить роль",
            description = "Меняет юзеру роль",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное изменение роли"),
                    @ApiResponse(responseCode = "403", description = "Нет прав управлять ролью", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Роль не найдена", content = @Content),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Юзер уже назначен на роль, "
                                    + "или кто-то внес изменения во время выполнения запроса",
                            content = @Content
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/edit")
    public RoleResponseDto editRole(@RequestBody @Valid RoleEditDto roleEditDto) {
        return roleService.editRole(roleEditDto);
    }

    @Operation(
            summary = "Получить роль по ID",
            description = "Возвращает роль по ее ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение роли"),
                    @ApiResponse(responseCode = "404", description = "Роль не найдена", content = @Content),
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/{id}")
    public RoleResponseDto getRole(@Parameter(description = "ID роли") @PathVariable UUID id) {
        return roleService.getRole(id);
    }

    @Operation(
            summary = "Получить все роли (связки юзера и роли в БД) юзера по его ID",
            description = "Возвращает список ролей юзера по его ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение ролей"),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content),
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/user/roles/{userId}")
    public List<RoleResponseDto> getRoleByUserId(@Parameter(description = "ID юзера") @PathVariable UUID userId) {
        return roleService.getUserRoles(userId);
    }

    @Operation(
            summary = "Получить все роли (названия, в том числе дочерние) юзера по его ID",
            description = "Возвращает список названий ролей юзера по его ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение ролей"),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content),
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/user/roles/all/{userId}")
    public List<String> getAllRolesByUserId(@Parameter(description = "ID юзера") @PathVariable UUID userId) {
        return roleService.getAllUserRoles(userId);
    }

    @Operation(
            summary = "Получить все роли (связки юзера и роли в БД) по имени енама роли",
            description = "Возвращает список ролей по имени енама роли",
            responses = @ApiResponse(responseCode = "200", description = "Успешное получение ролей"),
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/roles/{role}")
    public List<RoleResponseDto> getRolesByRole(@Parameter(description = "Роль (имя енама)") @PathVariable Roles role) {
        return roleService.getUsersWithRole(role);
    }

    @Operation(
            summary = "Удалить роль",
            description = "Удаляет роль с юзера (снимает с должности) по ID юзера и имени енама роли",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Успешное удаление роли"),
                    @ApiResponse(responseCode = "403", description = "Нет прав управлять ролью", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Не найден юзер", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Юзер не назначен на роль", content = @Content),
                    @ApiResponse(responseCode = "507", description = "Не удалось удалить роль", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteRole(
            @Parameter(description = "ID юзера") @PathVariable UUID userId,
            @Parameter(description = "Имя енама роли") @RequestParam(name = "role") Roles role
    ) {
        roleService.deleteRole(userId, role);
    }

}