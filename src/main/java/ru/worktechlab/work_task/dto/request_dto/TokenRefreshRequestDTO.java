package ru.worktechlab.work_task.dto.request_dto;

import lombok.Data;

@Data
public class TokenRefreshRequestDTO {
    private String refreshToken;
}
