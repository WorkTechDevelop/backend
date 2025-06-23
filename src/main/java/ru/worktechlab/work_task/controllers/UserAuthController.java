package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.TokenRefreshRequestDTO;
import ru.worktechlab.work_task.dto.response_dto.LoginResponseDTO;
import ru.worktechlab.work_task.services.AuthService;
import ru.worktechlab.work_task.services.UserService;

import static ru.worktechlab.work_task.models.enums.Roles.Fields.*;

@RestController
@RequestMapping("/work-task/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authenticate", description = "Аутентификация пользователей")
public class UserAuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Войти в учетную запись")
    public LoginResponseDTO authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для аутентификации",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequestDTO.class)
                    )
            )
            @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.authenticate(loginRequestDTO);
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        return authService.refreshAccessToken(request);
    }

    @Operation(summary = "Подтверждение почты пользователем")
    @GetMapping(value = "/confirm-email")
    public Boolean confirmEmail(@Parameter(description = "Токен подтверждения", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
                                @RequestParam String token) {
        return userService.emailConfirmation(token);
    }
}

