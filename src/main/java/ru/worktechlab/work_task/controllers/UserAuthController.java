package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.TokenRefreshRequestDTO;
import ru.worktechlab.work_task.dto.response_dto.LoginResponseDTO;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.services.AuthService;

@RestController
@RequestMapping("/work-task/v1/auth")
@AllArgsConstructor
@Tag(name = "Authenticate", description = "Аутентификация пользователей")
public class UserAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Войти в учетную запись")
    public ResponseEntity<LoginResponseDTO> authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для аутентификации",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequestDTO.class)
                    )
            )
            @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.authenticate(loginRequestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }
}

