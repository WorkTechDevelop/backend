package ru.worktechlab.work_task.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.model.db.TaskModel;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;
    private ValidateTask validateTask;

    @PostMapping("/createTask")
    public ResponseEntity<TaskCreationResponse> createTask(
            @RequestBody TaskModel taskModel,
            @RequestHeader("Authorization") String jwtToken) {

        log.info("Creating task with model: {}", taskModel);
        List<String> validationErrors = validateTask.validateTask(taskModel);

        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            return ResponseEntity.badRequest()
                    .body(new TaskCreationResponse(validationErrors));
        }

        try {
            String taskId = taskService.createTask(taskModel, jwtToken);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new TaskCreationResponse(taskId));
        } catch (RuntimeException e) {
            log.error("Error creating task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TaskCreationResponse(e.getMessage()));
        }
    }
}
