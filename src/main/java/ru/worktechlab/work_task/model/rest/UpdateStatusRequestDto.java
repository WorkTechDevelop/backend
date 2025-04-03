package ru.worktechlab.work_task.model.rest;

import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    private String id;
    private String status;
}
