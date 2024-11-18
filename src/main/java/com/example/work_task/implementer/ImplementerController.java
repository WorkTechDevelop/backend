package com.example.work_task.implementer;

import com.example.work_task.model.ImplementerModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/implementer")
@Slf4j
@AllArgsConstructor
public class ImplementerController {
    private final ImplementerService implementerService;

    @PostMapping("/create")
    public void createImplementer(@RequestBody ImplementerModel implementerModel) {
        implementerService.createImplementer(implementerModel);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handle(IllegalArgumentException e) {
        return "Текст ошибки который получит пользователь";
    }
}
