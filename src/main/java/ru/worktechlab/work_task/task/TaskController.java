package ru.worktechlab.work_task.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.model.db.TaskModel;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;
    private ValidateTask validateTask;

    @PostMapping("/create-task")
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody TaskModel taskModel,
            @RequestHeader("Authorization") String jwtToken) {
        return validateAndProcessTask(taskModel, jwtToken, true);
    }

    @PutMapping("/update-task")
    public ResponseEntity<TaskResponse> updateTask(
            @RequestBody TaskModel taskModel,
            @RequestHeader("Authorization") String jwtToken) {
        return validateAndProcessTask(taskModel, jwtToken, false);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable String id) {
        log.info("Получение задачи по id: {}", id);

        try {
            TaskModel task = taskService.getTaskById(id);
            return ResponseEntity.ok(new TaskResponse(task));
        } catch (RuntimeException e) {
            log.error("Ошибка при получении задачи: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TaskResponse("Задача не найдена: " + e.getMessage()));
        }
    }

    @GetMapping("/project-tasks/{projectId}")
    public ResponseEntity<List<TaskModel>> getTasksByProjectId(
            @PathVariable String projectId) {
        log.info("Получение задач по projectId: {}", projectId);

        try {
            List<TaskModel> tasks = taskService.getTasksByProjectId(projectId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении задач: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/main-page")
    public ResponseEntity<List<TaskModel>> getProjectTasksByUserGuid(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод задач проекта по guid пользователя");

        try {
            List<TaskModel> tasks = taskService.getProjectTaskByUserGuid(jwtToken);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении задач: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    private ResponseEntity<TaskResponse> validateAndProcessTask(
            TaskModel taskModel, String jwtToken, boolean isCreate) {
        log.info("Processing task with model: {}", taskModel);
        List<String> validationErrors = validateTask.validateTask(taskModel);

        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            return ResponseEntity.badRequest()
                    .body(new TaskResponse(validationErrors));
        }

        try {
            String taskId;
            if (isCreate) {
                taskId = taskService.createTask(taskModel, jwtToken);
            } else {
                taskId = taskService.updateTask(taskModel);
            }
            HttpStatus status = isCreate ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status)
                    .body(new TaskResponse(taskId));
        } catch (RuntimeException e) {
            String errorMessage = isCreate ? "creating" : "updating";
            log.error("Error {} task: {}", errorMessage, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TaskResponse("Internal server error: " + e.getMessage()));
        }
    }
}
