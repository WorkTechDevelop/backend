package ru.worktechlab.work_task.model.rest;

import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    private String code;
    private String status;
}
