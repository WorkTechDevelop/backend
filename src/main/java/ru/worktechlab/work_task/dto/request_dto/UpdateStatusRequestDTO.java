package ru.worktechlab.work_task.dto.request_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.validators.ValidTaskStatus;

@Schema(description = "Модель обновления статуса задачи")
@Data
public class UpdateStatusRequestDTO {

    @Schema(description = "Код задачи", example = "TCP-123")
    @NotBlank(message = "Поле CODE не может быть пустым")
    private String code;

    @Schema(description = "Новый статус задачи", example = "TODO")
    @NotBlank(message = "Поле STATUS не может быть пустым")
    @ValidTaskStatus
    private String status;
}
