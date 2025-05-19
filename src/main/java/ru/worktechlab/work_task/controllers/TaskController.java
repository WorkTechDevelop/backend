package ru.worktechlab.work_task.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.dto.response_dto.TaskModeResponselDTO;
import ru.worktechlab.work_task.dto.request_dto.TaskModelDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.services.TaskService;
import ru.worktechlab.work_task.dto.response.TaskResponse;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;

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

    @PutMapping("/update-status")
    public ResponseEntity<TaskModel> updateTask(
            @Valid
            @RequestBody UpdateStatusRequestDTO requestDto) {
        log.info("Обновить статус задачи");
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.updateTaskStatus(requestDto));
    }

    @GetMapping("/{code}")
    public ResponseEntity<TaskResponse> getTaskByCode(
            @PathVariable String code) {
        log.info("Получение задачи по коду: {}", code);
        TaskModel task = taskService.findTaskByCodeOrThrow(code);
        return ResponseEntity.ok(new TaskResponse(task));
    }

    @GetMapping("/project-tasks/{projectId}") //todo mb delete?
    public ResponseEntity<List<TaskModeResponselDTO>> getTasksByProjectId(
            @PathVariable String projectId) {
        log.info("Получение задач по projectId: {}", projectId);
        List<TaskModeResponselDTO> taskModeResponseDTO = taskService.getTasksModelResponseByProjectIdOrThrow(projectId);
        return ResponseEntity.ok(taskModeResponseDTO);
    }

    @GetMapping("/tasks-in-project")
    public ResponseEntity<List<UsersTasksInProjectDTO>> getTasksInProject(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех задач проекта отсартированных по пользователям");
        List<UsersTasksInProjectDTO> usersTasks = taskService.getProjectTaskByUserGuid(jwtToken);
        return ResponseEntity.ok(usersTasks);
    }
}
