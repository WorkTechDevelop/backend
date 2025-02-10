package ru.worktechlab.work_task.user;

import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.rest.LoginRequest;
import ru.worktechlab.work_task.model.rest.LoginResponse;
import ru.worktechlab.work_task.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class UserAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    private TaskService taskService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Добро пожаловать на борт!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String authAdmin() {
        return "Админ на базе";
    }

    @GetMapping("/default-user")
    @PreAuthorize("hasRole('PROJECT_MEMBER')")
    public String defaultUser() {
        return "Обычный гусь";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);


        LoginResponse response = new LoginResponse(userDetails.getFullUserName(), jwtToken);

        return ResponseEntity.ok(response);
    }
}
