package com.example.work_task.user;

import com.example.work_task.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class UserAuthController {

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
}
