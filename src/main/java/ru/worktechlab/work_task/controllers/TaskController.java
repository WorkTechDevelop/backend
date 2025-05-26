package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.request_dto.TaskModelDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.response.TaskResponse;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
@Tag(name = "Task", description = "Управление задачами")
public class TaskController {
    private TaskService taskService;

    @PostMapping("/create-task")
    @Operation(summary = "Создать задачу")
    public ResponseEntity<TaskResponse> createTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskModelDTO.class)
                    )
            )
            @RequestBody TaskModelDTO taskModelDTO,
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Processing create-task with model: {}", taskModelDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskModelDTO, jwtToken));
    }

    @PutMapping("/update-task")
    @Operation(summary = "Обновить задачу")
    public ResponseEntity<TaskResponse> updateTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateTaskModelDTO.class)
                    )
            )
            @RequestBody UpdateTaskModelDTO updateTaskModelDTO,
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Processing update-task with model: {}", updateTaskModelDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.updateTask(updateTaskModelDTO));
    }

    @PutMapping("/update-status")
    @Operation(summary = "Обновить статус задачи")
    public ResponseEntity<TaskModel> updateTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateStatusRequestDTO.class)
                    )
            )
            @RequestBody UpdateStatusRequestDTO requestDto) {
        log.info("Обновить статус задачи");
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.updateTaskStatus(requestDto));
    }

    @GetMapping("/{code}")
    @Operation(summary = "Получить задачу по коду {code}")
    public ResponseEntity<TaskResponse> getTaskByCode(
            @Parameter(
                    name = "code",
                    description = "Уникальный код задачи",
                    example = "TPO-0001"
            )
            @PathVariable String code) {
        log.info("Получение задачи по коду: {}", code);
        TaskModel task = taskService.findTaskByCodeOrThrow(code);
        return ResponseEntity.ok(new TaskResponse(task));
    }

    @GetMapping("/tasks-in-project")
    @Operation(summary = "Получить все задачи активного проекта отсортированные по пользователям")
    public ResponseEntity<List<UsersTasksInProjectDTO>> getTasksInProject(
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех задач проекта отсартированных по пользователям");
        List<UsersTasksInProjectDTO> usersTasks = taskService.getProjectTaskByUserGuid(jwtToken);
        return ResponseEntity.ok(usersTasks);
    }
}
