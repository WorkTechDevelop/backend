package ru.worktechlab.work_task.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Модель аутентификации пользователя")
public class LoginRequestDTO {
    @Schema(description = "Email пользователя", example = "user@gmail.com")
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}
