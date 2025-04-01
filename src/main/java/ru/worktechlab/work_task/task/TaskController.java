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

    @GetMapping("/main-page") //todo Модельки
    public ResponseEntity<?> getProjectTasksByUserGuid(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод главной страницы");
        return ResponseEntity.ok(mainPageService.getMainPageData(jwtToken));
    }
}
