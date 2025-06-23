package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.ApiResponse;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.task_comment.*;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryResponseDto;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.dto.tasks.TaskModelDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateTaskModelDTO;
import ru.worktechlab.work_task.exceptions.NotFoundException;
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
    public TaskDataDto createTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskModelDTO.class)
                    )
            )
            @RequestBody TaskModelDTO taskModelDTO) throws NotFoundException {
        return taskService.createTask(taskModelDTO);
    }

    @PutMapping("/update-task")
    @Operation(summary = "Обновить задачу")
    public TaskDataDto updateTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateTaskModelDTO.class)
                    )
            )
            @RequestBody UpdateTaskModelDTO updateTaskModelDTO) throws NotFoundException {
        return taskService.updateTask(updateTaskModelDTO);
    }

    @PutMapping("/update-status")
    @Operation(summary = "Обновить статус задачи")
    public TaskDataDto updateTaskStatus(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateStatusRequestDTO.class)
                    )
            )
            @RequestBody UpdateStatusRequestDTO requestDto) throws NotFoundException {
        return taskService.updateTaskStatus(requestDto);
    }

    @GetMapping("/tasks-in-project")
    @Operation(summary = "Получить все задачи активного проекта отсортированные по пользователям")
    public List<UsersTasksInProjectDTO> getTasksInProject() {
        return taskService.getProjectTaskByUserGuid();
    }


    @GetMapping("/history/{taskId}/{projectId}")
    @Operation(summary = "Получить историю изменения задачи по id {taskId}")
    public List<TaskHistoryResponseDto> getTaskHistory(
            @Parameter(description = "Уникальный идентификатор задачи",
                    example = "96cd710c-bd28-40b7-903e-4b8033892612",
                    required = true)
            @PathVariable("taskId") String taskId,
            @Parameter(description = "ИД проекта",
                    example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540",
                    required = true)
            @PathVariable("projectId") String projectId) throws NotFoundException {
        return taskHistoryService.getTaskHistoryById(taskId, projectId);
    }

    @PostMapping("/create-comment")
    @Operation(summary = "Создать комментарий")
    public CommentResponseDto createComment(@Valid
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    description = "Данные комментария",
                                                    content = @Content(
                                                            mediaType = "application/json",
                                                            schema = @Schema(implementation = CommentDto.class)
                                                    )
                                            )
                                            @RequestBody CommentDto commentDto) throws NotFoundException {
        return taskService.createComment(commentDto);
    }

    @PutMapping("/update-comment")
    @Operation(summary = "Обноаить комментарий")
    public CommentResponseDto updateComment(@Valid
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                    description = "Данные комментария",
                                                    content = @Content(
                                                            mediaType = "application/json",
                                                            schema = @Schema(implementation = UpdateCommentDto.class)
                                                    )
                                            )
                                            @RequestBody UpdateCommentDto dto) throws NotFoundException {
        return taskService.updateComment(dto);
    }

    @DeleteMapping("/delete-comment/{commentId}")
    @Operation(summary = "Удаление комментария")
    public ApiResponse deleteComment(
            @Parameter(description = "ИД комментария", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String commentId
    ) throws NotFoundException {
        return taskService.deleteCommentById(commentId);
    }

    @PostMapping("/all-comments")
    @Operation(summary = "Получить все комментарии к задаче")
    public List<GetAllTasksCommentsResponseDto> getAllTasksComments(@Valid
                                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                                            description = "id задачи и проекта",
                                                                            content = @Content(
                                                                                    mediaType = "application/json", schema = @Schema(implementation = GetAllTasksCommentsDto.class)
                                                                            )
                                                                    )
                                                                    @RequestBody GetAllTasksCommentsDto dto) throws NotFoundException {
        return taskService.getAllTasksComments(dto);
    }
}