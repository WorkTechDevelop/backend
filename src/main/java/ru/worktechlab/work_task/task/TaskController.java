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

import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;
    private MainPageService mainPageService;

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
        List<TaskModel> tasks = taskService.getTasksByProjectIdOrThrow(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks-in-project")
    public ResponseEntity<List<UsersTasksInProjectDTO>> getTasksInProject(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод главной страницы");
        return ResponseEntity.ok(mainPageService.getMainPageData(jwtToken));
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
