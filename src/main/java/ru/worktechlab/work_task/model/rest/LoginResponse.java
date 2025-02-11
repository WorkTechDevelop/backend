package ru.worktechlab.work_task.model.rest;

import lombok.Data;

@Data
public class LoginResponse {
    private String jwtToken;

    private String username;


    public LoginResponse(String username, String jwtToken) {
        this.username = username;
        this.jwtToken = jwtToken;
    }
}
