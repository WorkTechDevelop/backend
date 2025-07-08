package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.ApiResponse;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.task_comment.AllTasksCommentsResponseDto;
import ru.worktechlab.work_task.dto.task_comment.CommentDto;
import ru.worktechlab.work_task.dto.task_comment.CommentResponseDto;
import ru.worktechlab.work_task.dto.task_comment.UpdateCommentDto;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryResponseDto;
import ru.worktechlab.work_task.dto.task_link.LinkDto;
import ru.worktechlab.work_task.dto.task_link.LinkResponseDto;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.dto.tasks.TaskModelDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateTaskModelDTO;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.services.TaskHistoryService;
import ru.worktechlab.work_task.services.TaskService;

import java.util.List;

import static ru.worktechlab.work_task.models.enums.Roles.Fields.*;

@RestController
@RequestMapping("work-task/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task", description = "Управление задачами")
public class TaskController {
    private final TaskService taskService;
    private final TaskHistoryService taskHistoryService;

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @PostMapping("/create")
    @Operation(summary = "Создать задачу")
    @ResponseStatus(HttpStatus.CREATED)
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

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @PutMapping("/{projectId}/{taskId}/update")
    @Operation(summary = "Обновить задачу")
    public TaskDataDto updateTask(
            @Parameter(description = "Уникальный идентификатор задачи",
                    example = "96cd710c-bd28-40b7-903e-4b8033892612",
                    required = true)
            @PathVariable("taskId") String taskId,
            @Parameter(description = "ИД проекта",
                    example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540",
                    required = true)
            @PathVariable("projectId") String projectId,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateTaskModelDTO.class)
                    )
            )
            @RequestBody UpdateTaskModelDTO updateTaskModelDTO) throws NotFoundException {
        return taskService.updateTask(projectId, taskId, updateTaskModelDTO);
    }

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
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

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @GetMapping("/tasks-in-project")
    @Operation(summary = "Получить все задачи активного проекта отсортированные по пользователям")
    public List<UsersTasksInProjectDTO> getTasksInProject() {
        return taskService.getProjectTaskByUserGuid();
    }

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @GetMapping("/{projectId}/{taskId}/history")
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

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
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

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @PutMapping("/update-comment")
    @Operation(summary = "Обновить комментарий")
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

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @DeleteMapping("/{commentId}/{taskId}/{projectId}/delete-comment")
    @Operation(summary = "Удаление комментария")
    public ApiResponse deleteComment(
            @Parameter(description = "ИД комментария", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String commentId,
            @Parameter(description = "Уникальный идентификатор задачи",
                    example = "96cd710c-bd28-40b7-903e-4b8033892612",
                    required = true)
            @PathVariable("taskId") String taskId,
            @Parameter(description = "ИД проекта",
                    example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540",
                    required = true)
            @PathVariable("projectId") String projectId
    ) throws NotFoundException {
        return taskService.deleteComment(commentId, taskId, projectId);
    }

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @GetMapping("/{taskId}/{projectId}/comments")
    @Operation(summary = "Получить все комментарии к задаче")
    public List<AllTasksCommentsResponseDto> allTasksComments(@PathVariable("taskId") String taskId,
                                                              @Parameter(description = "ИД проекта",
                                                                      example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540",
                                                                      required = true)
                                                              @PathVariable("projectId") String projectId) throws NotFoundException {
        return taskService.allTasksComments(taskId, projectId);
    }

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @PostMapping("/create-link")
    @Operation(summary = "Создать связь между задачами")
    public LinkResponseDto linkTask(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Модель связывания задач",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = LinkDto.class)
                    )
            )
            @RequestBody LinkDto dto
    ) throws NotFoundException {
        return taskService.linkTask(dto);
    }

    @RolesAllowed({PROJECT_MEMBER, PROJECT_OWNER, POWER_USER})
    @GetMapping("/{taskId}/{projectId}/links")
    public List<LinkResponseDto> allTasksLinks(
            @PathVariable("taskId") String taskId,
            @Parameter(description = "ИД проекта",
                    example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540",
                    required = true)
            @PathVariable("projectId") String projectId
    ) throws NotFoundException {
        return taskService.allTasksLinks(taskId, projectId);
    }
}