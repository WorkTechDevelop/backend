package ru.worktechlab.work_task.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.models.request_dto.RegisterDTO;
import ru.worktechlab.work_task.services.UserService;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class AccountController {

    private final UserService userService;

    @PostMapping("/registry")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDTO registerDto) {
        userService.registerUser(registerDto);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }
}