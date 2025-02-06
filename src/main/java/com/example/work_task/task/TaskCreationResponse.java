package com.example.work_task.task;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskCreationResponse {
    private String taskId;
    private List<String> errors;

    public TaskCreationResponse(String taskId) {
        this.taskId = taskId;
    }

    public TaskCreationResponse(List<String> errors) {
        this.errors = errors;
    }

}
