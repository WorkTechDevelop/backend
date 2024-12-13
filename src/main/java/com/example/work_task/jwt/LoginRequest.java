package com.example.work_task.jwt;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    private String password;
}
