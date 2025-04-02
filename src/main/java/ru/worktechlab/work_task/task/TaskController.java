package ru.worktechlab.work_task.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.responseDTO.SprintInfoDTO;
import ru.worktechlab.work_task.responseDTO.UsersProjectsDTO;
import ru.worktechlab.work_task.responseDTO.UsersTasksInProjectDTO;

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

    @GetMapping("/tasks-in-project")
    public ResponseEntity<List<UsersTasksInProjectDTO>> getTasksInProject(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех задач проекта отсартированных по пользователям");
       try {
           List<UsersTasksInProjectDTO> usersTasks = taskService.getProjectTaskByUserGuid(jwtToken);
           return ResponseEntity.ok(usersTasks);
       } catch (RuntimeException e) {
           log.error("Ошибка при получении задач: {}", e.getMessage(), e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Collections.emptyList());
       }
    }

    @GetMapping("/sprint-info")
    public ResponseEntity<SprintInfoDTO> getSprintInfo(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод информации о спринте");
        try {
            SprintInfoDTO sprintInfo = taskService.getSprintName(jwtToken);
            return ResponseEntity.ok(sprintInfo);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении информации о спринте: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/users-projects")
    public ResponseEntity<List<UsersProjectsDTO>> getProjectByUser(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех проектов пользователя");
        try {
            List<UsersProjectsDTO> projects = taskService.getUserProject(jwtToken);
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении проектов: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/active-project")
    public ResponseEntity<String> getActiveProject(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Получить id активного проекта");
        try {
            String activeProject = taskService.getLastProjectId(jwtToken);
            return ResponseEntity.ok(activeProject);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении активного проекта: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
