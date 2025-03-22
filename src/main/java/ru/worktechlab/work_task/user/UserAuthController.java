package ru.worktechlab.work_task.user;

import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.rest.LoginRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.user.service.AuthService;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class UserAuthController {

    private final AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/welcome")
    public String welcome() {
        return "Добро пожаловать на борт!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }
}
