package ru.worktechlab.work_task.dto.task_comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import ru.worktechlab.work_task.validators.ValidTaskId;

public class UpdateCommentResponseDto {
    @Schema(description = "ID комментария")
    @NotBlank(message = "COMMENT_ID не может быть пустым")
    private String commentId;

    @Schema(description = "ID задачи")
    @NotBlank(message = "Поле taskId не может быть пустым")
    @ValidTaskId
    private String taskId;

    @Schema(description = "Комментарий")
    @NotBlank(message = "COMMENT не может быть пустым")
    private String comment;
}
