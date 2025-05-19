package ru.worktechlab.work_task.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.validators.*;

@Data
public class UpdateTaskModelDTO {

    @NotBlank(message = "Поле ID не может быть пустым")
    @ValidTaskId
    private String id;

    @NotBlank(message = "Поле TITLE не может быть пустым")
    @Size(max = 255, message = "Длина поля TITLE не может быть более 255 символов")
    private String title;

    @ValidDescription
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

    @ValidSprintId
    private String sprintId;

    @NotBlank(message = "Поле TASK_TYPE не может быть пустым")
    @ValidTaskType
    private String taskType;

    @NotBlank(message = "Поле STATUS не может быть пустым")
    @ValidTaskStatus
    private String status;

    @ValidEstimation
    private Integer estimation;

    @NotNull(message = "Поле CODE не может быть пустым")
    private String code;
}