package ru.worktechlab.work_task.models.response_dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String jwtToken;

    private String username;


    public LoginResponseDTO(String username, String jwtToken) {
        this.username = username;
        this.jwtToken = jwtToken;
    }
}
