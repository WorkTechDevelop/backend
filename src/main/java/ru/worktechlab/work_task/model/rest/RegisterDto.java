package ru.worktechlab.work_task.model.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.worktechlab.work_task.user.PasswordMatch;
import ru.worktechlab.work_task.user.ValidGender;

@Data
@PasswordMatch
public class RegisterDto {

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    private String password;

    @NotBlank(message = " Поле не может быть пустым")
    private String confirmPassword;
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустым")
    private String lastName;

    @NotBlank(message = "Отчество не может быть пустым")
    private String middleName;

    private String phone;

    private String birthDate;

    @NotBlank(message = "Gender не может быть пустым")
    @ValidGender
    private String gender;
}
