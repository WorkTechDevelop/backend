package ru.worktechlab.work_task.task;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
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
