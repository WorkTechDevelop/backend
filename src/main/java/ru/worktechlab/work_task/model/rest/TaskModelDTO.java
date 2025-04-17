package ru.worktechlab.work_task.model.rest;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.worktechlab.work_task.task.validators.*;

@Data
public class TaskModelDTO {

    @NotBlank(message = "Поле TITLE не может быть пустым")
    @Size(max = 255, message = "Длина поля TITLE не может быть более 255 символов")
    private String title;

    @NotBlank(message = "Поле DESCRIPTION не может быть пустым")
    @Size(max = 4096, message = "Длина поля DESCRIPTION не может быть более 4096 символов")
    private String description;

    @NotBlank(message = "Поле PRIORITY не может быть пустым")
    @ValidPriority
    private String priority;

    @ValidAssignee
    @NotBlank(message = "ASSIGNEE не может быть пустым")
    private String assignee;

    @NotNull(message = "Поле PROJECT_ID не может быть пустым")
    @ValidProjectId
    private String projectId;

    @NotNull(message = "Поле SPRINT_ID не может быть пустым")
    @ValidSprintId
    private String sprintId;

    @NotBlank(message = "Поле TASK_TYPE не может быть пустым")
    @ValidTaskType
    private String taskType;

    @NotNull(message = "Поле ESTIMATION не может быть пустым")
    @Min(value = 1, message = "ESTIMATION должен быть больше 0")
    @Max(value = 20, message = "ESTIMATION должен быть меньше 20")
    private Integer estimation;
}
