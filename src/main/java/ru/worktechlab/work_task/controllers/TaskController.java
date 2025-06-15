package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.request_dto.TaskModelDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.response.TaskResponse;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryResponseDto;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.services.TaskHistoryService;
import ru.worktechlab.work_task.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Управление задачами")
public class TaskController {
    private final TaskService taskService;
    private final TaskHistoryService taskHistoryService;

    @PostMapping("/create-task")
    @Operation(summary = "Создать задачу")
    public TaskResponse createTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskModelDTO.class)
                    )
            )
            @RequestBody TaskModelDTO taskModelDTO) {
        return taskService.createTask(taskModelDTO);
    }

    @PutMapping("/update-task")
    @Operation(summary = "Обновить задачу")
    public TaskResponse updateTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateTaskModelDTO.class)
                    )
            )
            @RequestBody UpdateTaskModelDTO updateTaskModelDTO) {
        return taskService.updateTask(updateTaskModelDTO);
    }

    @PutMapping("/update-status")
    @Operation(summary = "Обновить статус задачи")
    public TaskModel updateTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateStatusRequestDTO.class)
                    )
            )
            @RequestBody UpdateStatusRequestDTO requestDto) {
        return taskService.updateTaskStatus(requestDto);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Получить задачу по коду {code}")
    public TaskResponse getTaskByCode(
            @Parameter(
                    name = "code",
                    description = "Уникальный код задачи",
                    example = "TPO-0001"
            )
            @PathVariable String code) {
        return new TaskResponse(taskService.findTaskByCodeOrThrow(code));
    }

    @GetMapping("/tasks-in-project")
    @Operation(summary = "Получить все задачи активного проекта отсортированные по пользователям")
    public List<UsersTasksInProjectDTO> getTasksInProject() {
        return taskService.getProjectTaskByUserGuid();
    }


    @GetMapping("/history/{taskId}")
    @Operation(summary = "Получить историю изминения задачи по id {taskId}")
    public List<TaskHistoryResponseDto> getTaskHistory(
            @Parameter(
                    name = "taskId",
                    description = "Уникальный идентификатор задачи",
                    example = "96cd710c-bd28-40b7-903e-4b8033892612"
            )
            @PathVariable String taskId) {
        return taskHistoryService.getTaskHistoryById(taskId);
    }
}