package com.example.work_task.model;

import lombok.Data;

@Data
public class TaskModel {
    private String id;
    private String title;
    private String description;
    private String implementer;
    private String status;
}
