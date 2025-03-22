package ru.worktechlab.work_task.user;

import ru.worktechlab.work_task.model.rest.LoginRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.user.service.AuthService;

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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }
}
