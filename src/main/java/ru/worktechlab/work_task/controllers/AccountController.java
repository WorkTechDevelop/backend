package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.dto.request_dto.RegisterDTO;
import ru.worktechlab.work_task.services.UserService;

@RestController
@RequestMapping("/work-task/v1/registration")
@AllArgsConstructor
@Tag(name = "Registration", description = "Регистрация пользователя")
public class AccountController {

    private final UserService userService;

    @PostMapping("/registry")
    @Operation(summary = "Зарегистрироваться")
    public ResponseEntity<String> registerUser(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterDTO.class)
                    )
            )
            @RequestBody RegisterDTO registerDto) {
        userService.registerUser(registerDto);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }
}