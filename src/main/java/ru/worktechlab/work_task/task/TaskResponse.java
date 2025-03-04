package ru.worktechlab.work_task.task;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.worktechlab.work_task.model.db.TaskModel;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TaskResponse {
    private TaskModel task;
    private String taskId;
    private List<String> errors;

    public TaskResponse(String taskId) {
        this.taskId = taskId;
    }

    public TaskResponse(List<String> errors) {
        this.errors = errors;
    }

    public TaskResponse(TaskModel task) {
        this.task = task;
    }
}
