package ru.worktechlab.work_task.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.model.rest.RegisterDto;
import ru.worktechlab.work_task.user.service.UserService;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class AccountController {

    private final UserService userService;

    @PostMapping("/registry")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        userService.registerUser(registerDto);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }
}