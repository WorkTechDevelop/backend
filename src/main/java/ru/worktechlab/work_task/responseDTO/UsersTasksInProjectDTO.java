package ru.worktechlab.work_task.responseDTO;

import lombok.Data;
import ru.worktechlab.work_task.model.db.TaskModel;

import java.util.List;

@Data
public class UsersTasksInProjectDTO {
    private String userName;
    private List<TaskModel> tasks;

    public UsersTasksInProjectDTO(String userName, TaskModel taskModel) {
        this.userName = userName;
        this.tasks = List.of(taskModel);
    }

    public UsersTasksInProjectDTO(String userName, List<TaskModel> tasks) {
        this.userName = userName;
            this.tasks = tasks;
    }
}
