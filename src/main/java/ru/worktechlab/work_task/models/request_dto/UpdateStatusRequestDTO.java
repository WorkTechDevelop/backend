package ru.worktechlab.work_task.models.request_dto;

import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    private String code;
    private String status;
}
