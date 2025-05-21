package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.services.AuthService;

@RestController
@RequestMapping("/work-task/v1/auth")
@AllArgsConstructor
@Tag(name = "Authenticate", description = "Аутентификация пользователей")
public class UserAuthController {

    private final AuthService authService;

    @GetMapping("/welcome")
    @Operation(
            summary = "Добро пожаловать",
            description = "Отображение страницы для неавторизованных пользователей"
    )
    public String welcome() {
        return "Добро пожаловать на борт!";
    }

    @PostMapping("/login")
    @Operation(summary = "Войти в учетную запись")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.authenticate(loginRequestDTO));
    }
}
