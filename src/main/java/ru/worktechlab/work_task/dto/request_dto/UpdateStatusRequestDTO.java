package ru.worktechlab.work_task.dto.request_dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.validators.ValidTaskStatus;

@Data
public class UpdateStatusRequestDTO {
    @NotBlank(message = "Поле CODE не может быть пустым")
    private String code;

    @NotBlank(message = "Поле STATUS не может быть пустым")
    @ValidTaskStatus
    private String status;
}
