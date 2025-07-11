package ru.worktechlab.work_task.dto.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest {

    @Schema(description = "Фамилия пользователя", example = "Пин")
    @NotBlank(message = "Фамилия не может быть пустым")
    private String lastName;

    @Schema(description = "Имя пользователя", example = "Ян")
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @Schema(description = "Отчество пользователя, при наличии", example = "Сигизмундович")
    private String middleName;

    @Schema(description = "Email пользователя", example = "user@gmail.com")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @Schema(description = "Номер телефона пользователя", example = "89999999999")
    private String phone;

    @Schema(description = "Дата рождения пользователя", example = "2020-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "Пароль пользователя", example = "password123")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String password;

    @Schema(description = "подтверждение пароля", example = "password123")
    @NotBlank(message = " Поле не может быть пустым")
    private String confirmPassword;
}
