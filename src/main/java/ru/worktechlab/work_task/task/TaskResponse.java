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
    private List<TaskModel> tasks;
    private String sprintName;
    private List<Object[]> projects;
    private String activeProject;

    public TaskResponse(String taskId) {
        this.taskId = taskId;
    }

    public TaskResponse(List<String> errors) {
        this.errors = errors;
    }

    public TaskResponse(TaskModel task) {
        this.task = task;
    }

    public TaskResponse(List<TaskModel> tasks, String sprintName, List<Object[]> projects, String activeProject) {
        this.tasks = tasks;
        this.sprintName = sprintName;
        this.projects = projects;
        this.activeProject = activeProject;
    }
}
