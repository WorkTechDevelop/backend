package com.example.work_task.user;

import com.example.work_task.task.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work")
@AllArgsConstructor
public class UserAuthController {

    private TaskService taskService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Добро пожаловать на борт, сучата!";
    }
}
