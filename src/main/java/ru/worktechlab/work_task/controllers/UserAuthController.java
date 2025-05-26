package ru.worktechlab.work_task.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.TokenRefreshRequestDTO;
import ru.worktechlab.work_task.dto.response_dto.LoginResponseDTO;
import ru.worktechlab.work_task.services.AuthService;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class UserAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.authenticate(loginRequestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }
}

