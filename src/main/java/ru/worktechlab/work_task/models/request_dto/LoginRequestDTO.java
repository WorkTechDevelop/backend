package ru.worktechlab.work_task.models.request_dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;

    private String password;
}
