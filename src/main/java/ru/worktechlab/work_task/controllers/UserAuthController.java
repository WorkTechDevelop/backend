package ru.worktechlab.work_task.controllers;

import ru.worktechlab.work_task.models.request_dto.LoginRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.services.AuthService;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class UserAuthController {

    private final AuthService authService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Добро пожаловать на борт!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.authenticate(loginRequestDTO));
    }
}
