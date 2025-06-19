package ru.worktechlab.work_task.dto.request_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.worktechlab.work_task.validators.*;

@Schema(description = "Модель обновления задачи")
@Data
public class UpdateTaskModelDTO {

    @Schema(description = "id задачи")
    @NotBlank(message = "Поле ID не может быть пустым")
    @ValidTaskId
    private String id;

    @Schema(description = "Заголовок", example = "Создание задачи")
    @NotBlank(message = "Поле TITLE не может быть пустым")
    @Size(max = 255, message = "Длина поля TITLE не может быть более 255 символов")
    private String title;

    @Schema(description = "Описание", example = "Создать задачу для Яна")
    @ValidDescription
    private String description;

    @Schema(description = "Приоритет", example = "MINOR")
    @NotBlank(message = "Поле PRIORITY не может быть пустым")
    @ValidPriority
    private String priority;

    @Schema(description = "Исполнитель задачи", example = "id пользователя")
    @ValidAssignee
    @NotBlank(message = "ASSIGNEE не может быть пустым")
    private String assignee;

    @Schema(description = "Проект задачи", example = "id проекта")
    @NotNull(message = "Поле PROJECT_ID не может быть пустым")
    @ValidProjectId
    private String projectId;

    @Schema(description = "Спринт", example = "id спринта")
    @ValidSprintId
    private String sprintId;

    @Schema(description = "Тип задачи", example = "TASK")
    @NotBlank(message = "Поле TASK_TYPE не может быть пустым")
    @ValidTaskType
    private String taskType;

    @NotBlank(message = "Поле STATUS не может быть пустым")
    @ValidTaskStatus
    private String status;

    @Schema(description = "Оценка задачи", example = "3")
    @ValidEstimation
    private Integer estimation;
}