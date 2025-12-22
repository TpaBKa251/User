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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.internal.utils.Roles;
import ru.tpu.hostel.user.dto.request.UserRegisterDto;
import ru.tpu.hostel.user.dto.response.UserNameResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseDto;
import ru.tpu.hostel.user.dto.response.UserResponseWithRoleDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto;
import ru.tpu.hostel.user.dto.response.UserShortResponseDto2;
import ru.tpu.hostel.user.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Tag(name = "Юзеры", description = "Эндпоинты для работы с юзерами")
@ApiResponse(responseCode = "500", description = "Неизвестная ошибка сервера", content = @Content)
@ApiResponse(
        responseCode = "400",
        description = "Неверный запрос от клиента, нарушение ограничений запроса (тело, параметры)",
        content = @Content
)
public class UserController {

    private final UserServiceImpl userService;

    @Operation(
            summary = "Создать (зарегистрировать) юзера",
            description = "Создает нового юзера",
            responses = @ApiResponse(responseCode = "201", description = "Успешное создание (регистрация) юзера")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponseDto registerUser(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        return userService.registerUser(userRegisterDto);
    }

    @Operation(
            summary = "Получить инфу о текущем юзере",
            description = "Возвращает всю инфу о текущем юзере (отправившем запрос)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзере"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/profile")
    public UserResponseDto getUser() {
        return userService.getUser();
    }

    @Operation(
            summary = "Получить инфу о юзере по ID",
            description = "Возвращает всю инфу о юзере по ID, но кроме ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзере"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/by/id/{id}")
    public UserShortResponseDto getUserById(@Parameter(description = "ID юзера") @PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Получить краткую инфу о юзере по ID",
            description = "Возвращает краткую инфу о юзере по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение краткой инфы о юзере"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/by/id/short/{id}")
    public UserShortResponseDto2 getUserByIdShort(@Parameter(description = "ID юзера") @PathVariable UUID id) {
        return userService.getUserByIdShort(id);
    }

    @Operation(
            summary = "Получить инфу с ролями о юзере по ID",
            description = "Возвращает инфу с ролями о юзере по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзере"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/with/roles/{id}")
    public UserResponseWithRoleDto getUserWithRoles(@Parameter(description = "ID юзера") @PathVariable UUID id) {
        return userService.getUserWithRole(id);
    }

    @Operation(
            summary = "Получить инфу о всех юзерах",
            description = "Постранично возвращает всю инфу о всех юзерах по набору параметров (список)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/all")
    public List<UserResponseDto> getAllUsers(
            @Parameter(description = "Номер страницы") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Количество записей на странице")
            @RequestParam(required = false, defaultValue = "1000000000")
            int size,
            @Parameter(description = "Имя") @RequestParam(required = false, defaultValue = "") String firstName,
            @Parameter(description = "Фамилия") @RequestParam(required = false, defaultValue = "") String lastName,
            @Parameter(description = "Отчество") @RequestParam(required = false, defaultValue = "") String middleName,
            @Parameter(description = "Номер комнаты") @RequestParam(required = false, defaultValue = "") String room
    ) {
        return userService.getAllUsers(
                page,
                size,
                firstName,
                lastName,
                middleName,
                room
        );
    }

    @Operation(
            summary = "Получить список из части ФИО",
            description = "Возвращает список из имен или фамилий, или отчеств по параметрам. Если указано имя, "
                    + "то возвращается список их похожих имен, аналогично с фамилией и отчеством. Пример: firstName=ва,"
                    + " ответ=[Вадим, Вован]",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение частей ФИО"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get")
    public List<String> getAllUsersWithName(
            @Parameter(description = "Имя") @RequestParam(required = false) String firstName,
            @Parameter(description = "Фамилия") @RequestParam(required = false) String lastName,
            @Parameter(description = "Отчество") @RequestParam(required = false) String middleName
    ) {
        return userService.getNamesLike(firstName, lastName, middleName);
    }

    @Operation(
            summary = "Получить список из всей инфы о юзерах с ID",
            description = "Возвращает список из полной инфы о юзерах с указанными ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/all/by/ids")
    public List<UserResponseDto> getAllUsersWithIds(
            @Parameter(description = "Список ID юзеров") @RequestParam UUID[] ids
    ) {
        return userService.getAllUsersByIds(Arrays.stream(ids).toList());
    }

    @Operation(
            summary = "Получить список из краткой инфы о юзерах с ID",
            description = "Возвращает список из краткой инфы о юзерах с указанными ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/all/by/ids/short")
    public List<UserShortResponseDto2> getAllUsersWithIdsShort(
            @Parameter(description = "Список ID юзеров") @RequestParam UUID[] ids
    ) {
        return userService.getAllUsersByIdsShort(Arrays.stream(ids).toList());
    }

    @Operation(
            summary = "Получить список из краткой инфы о юзерах с ФИО",
            description = "Постранично возвращает список из краткой инфы о юзерах с похожим ФИО",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/by/name")
    public List<UserShortResponseDto2> getUsersByName(
            @Parameter(description = "ФИО") @RequestParam(required = false, defaultValue = "") String name,
            @Parameter(description = "Номер страницы") @RequestParam int page,
            @Parameter(description = "Количество записей на странице") @RequestParam int size

    ) {
        return userService.getUserByName(name, page, size);
    }

    @Operation(
            summary = "Получить список из краткой инфы о юзерах с ролью",
            description = "Постранично возвращает список из краткой инфы о юзерах с ролью. Если указать параметр "
                    + "onMyFloor, то вернется список для юзеров с того же этажа, что и текущий (отправивший запрос)."
                    + " Пагинация при этом игнорируется",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/by/role")
    public List<UserShortResponseDto2> getUsersByRole(
            @Parameter(description = "Роль") @RequestParam Roles role,
            @Parameter(description = "Номер страницы") @RequestParam int page,
            @Parameter(description = "Количество записей на странице") @RequestParam int size,
            @Parameter(description = "Указывает на поиск в рамках этажа текущего юзера. Отключает пагинацию")
            @RequestParam(name = "onMyFloor", required = false, defaultValue = "false")
            boolean onMyFloor
    ) {
        return userService.getAllUsersByRole(role, page, size, onMyFloor);
    }

    @Operation(
            summary = "Получить список из краткой инфы о юзерах с ФИО и ролью",
            description = "Постранично возвращает список из краткой инфы о юзерах с похожим ФИО и ролью",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/by/name/with/role")
    public List<UserShortResponseDto2> getUsersByNameWithRole(
            @Parameter(description = "ФИО") @RequestParam(required = false, defaultValue = "") String name,
            @Parameter(description = "Роль") @RequestParam Roles role,
            @Parameter(description = "Номер страницы") @RequestParam int page,
            @Parameter(description = "Количество записей на странице") @RequestParam int size
    ) {
        return userService.getUserByNameWithRole(name, role, page, size);
    }

    @Operation(
            summary = "Получить список из краткой инфы о юзерах с ФИО и без роли",
            description = "Постранично возвращает список из краткой инфы о юзерах с похожим ФИО и без роли",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/by/name/without/role")
    public List<UserShortResponseDto2> getUsersByNameWithoutRole(
            @Parameter(description = "ФИО") @RequestParam(required = false, defaultValue = "") String name,
            @Parameter(description = "Роль") @RequestParam Roles role,
            @Parameter(description = "Номер страницы") @RequestParam int page,
            @Parameter(description = "Количество записей на странице") @RequestParam int size
    ) {
        return userService.getUserByNameWithoutRole(name, role, page, size);
    }

    @Operation(
            summary = "Получить список из инфы с ФИО и контактами о юзерах на этаже",
            description = "Постранично возвращает список из инфы с ФИО и контактами о юзерах, которые живут на том же "
                    + "этаже, что юзер, чей ID указан",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/all/on/floor/{userId}")
    public List<UserNameResponseDto> getAllOnFloor(
            @Parameter(description = "ID юзера") @PathVariable UUID userId,
            @Parameter(description = "Номер страницы") @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "Количество записей на странице")
            @RequestParam(required = false, defaultValue = "10")
            int size
    ) {
        return userService.getAllUsersOnFloor(userId, page, size);
    }

    @Operation(
            summary = "Получить номер комнаты юзера",
            description = "Возвращает номер комнаты юзера с указанным ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Юзер не найден", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/room/{userId}")
    public String getRoomNumber(@Parameter(description = "ID юзера") @PathVariable UUID userId) {
        return userService.getRoomNumberByUserId(userId);
    }

    @Operation(
            summary = "Получить список из инфы с ФИО и контактами о юзерах в комнатах",
            description = "Возвращает список из инфы с ФИО и контактами о юзерах, которые живут в указанных комнатах",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/all/in/rooms")
    public List<UserNameResponseDto> getAllInRooms(
            @Parameter(description = "Номера комнат") @RequestParam String[] roomNumbers
    ) {
        return userService.getAllUsersInRooms(Arrays.stream(roomNumbers).toList());
    }

    @Operation(
            summary = "Получить список ID юзеров в комнатах",
            description = "Возвращает список из ID юзеров, которые живут в указанных комнатах",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение инфы о юзерах"),
                    @ApiResponse(responseCode = "401", description = "Запрос не авторизован", content = @Content)
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/get/all/in/rooms/with/id")
    public List<UUID> getAllInRoomsWithId(
            @Parameter(description = "Номера комнат") @RequestParam String[] roomNumbers
    ) {
        return userService.getAllIdsOfUsersInRooms(Arrays.stream(roomNumbers).toList());
    }

}