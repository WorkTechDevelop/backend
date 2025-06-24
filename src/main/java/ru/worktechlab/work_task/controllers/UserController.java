package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.OkResponse;
import ru.worktechlab.work_task.dto.StringIdsDto;
import ru.worktechlab.work_task.dto.users.UserShortDataDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.services.UserService;

import java.util.List;

import static ru.worktechlab.work_task.models.enums.Roles.Fields.*;

@RestController
@RequestMapping("/work-task/v1/user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Управление с пользователями")
public class UserController {

    private final UserService userService;

    @RolesAllowed({ADMIN, PROJECT_OWNER})
    @Operation(summary = "Список всех пользователей")
    @GetMapping
    public List<UserShortDataDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @Operation(summary = "Список всех пользователей по существующим ИД")
    @PostMapping()
    public List<UserShortDataDto> findUsersByIdsIn(
            @Parameter(description = "Идентификаторы пользователей", example = "[\"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", \"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", ...]")
            @RequestBody StringIdsDto data) throws NotFoundException {
        return userService.findUsersByIdsIn(data);
    }

    @RolesAllowed({ADMIN, PROJECT_OWNER})
    @Operation(summary = "Активировать пользователей по существующим ИД")
    @PutMapping("/activate")
    public OkResponse activateUsers(
            @Parameter(description = "Идентификаторы пользователей", example = "[\"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", \"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", ...]")
            @RequestBody StringIdsDto data) throws NotFoundException {
        return userService.activateUsers(data, true);
    }

    @RolesAllowed({ADMIN, PROJECT_OWNER})
    @Operation(summary = "Заблокировать пользователей по существующим ИД")
    @PutMapping("/block")
    public OkResponse blockUsers(
            @Parameter(description = "Идентификаторы пользователей", example = "[\"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", \"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", ...]")
            @RequestBody StringIdsDto data) throws NotFoundException {
        return userService.activateUsers(data, false);
    }
}
