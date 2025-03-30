package ru.worktechlab.work_task.task;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.rest.TaskModelDTO;
import ru.worktechlab.work_task.model.rest.UpdateTaskModelDTO;
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
            @Valid
            @RequestBody TaskModelDTO taskModelDTO,
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Processing create-task with model: {}", taskModelDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskModelDTO, jwtToken));
    }

    @PutMapping("/update-task")
    public ResponseEntity<TaskResponse> updateTask(
            @Valid
            @RequestBody UpdateTaskModelDTO updateTaskModelDTO,
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Processing update-task with model: {}", updateTaskModelDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.updateTask(updateTaskModelDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable String id) {
        log.info("Получение задачи по id: {}", id);

        TaskModel task = taskService.findTaskByIdOrThrow(id);
        return ResponseEntity.ok(new TaskResponse(task));
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

    @GetMapping("/main-page") //todo Модельки
    public ResponseEntity<?> getProjectTasksByUserGuid(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод главной страницы");

        try {
            List<UsersTasksInProjectDTO> usersTasks = taskService.getProjectTaskByUserGuid(jwtToken);
            SprintInfoDTO sprintInfo = taskService.getSprintName(jwtToken);
            List<UsersProjectsDTO> projects = taskService.getUserProject(jwtToken);
            String activeProject = taskService.getLastProjectId(jwtToken);
            TaskResponse response = new TaskResponse(usersTasks, sprintInfo, projects, activeProject);
            return ResponseEntity.ok(response);
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
