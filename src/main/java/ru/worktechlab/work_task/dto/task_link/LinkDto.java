package ru.worktechlab.work_task.dto.task_link;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.worktechlab.work_task.validators.ValidProjectId;
import ru.worktechlab.work_task.validators.ValidTaskId;

@Schema(description = "Модель связывания задач")
@Getter
@Setter
public class LinkDto {

    @Schema(description = "ID задачи")
    @NotBlank(message = "Поле taskId не может быть пустым")
    @ValidTaskId
    private String taskIdFirst;

    @Schema(description = "ID задачи")
    @NotBlank(message = "Поле taskId не может быть пустым")
    @ValidTaskId
    private String taskIdSecond;

    @Schema(description = "Проект задачи")
    @NotBlank(message = "Поле PROJECT_ID не может быть пустым")
    @ValidProjectId
    private String projectId;

    @Schema(description = "id LinkType", example = "1, 2, 3")
    @NotNull(message = "linkType не может быть пустым")
    private Long linkTypeId;
}