package ru.worktechlab.work_task.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.dto.response_dto.SprintInfoDTO;
import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TaskResponse {
    private TaskModel task;
    private String taskId;
    private List<String> errors;
    private List<TaskModel> tasks;
    private SprintInfoDTO sprintInfo;
    private List<UsersProjectsDTO> projects;
    private String activeProject;
    private List<UsersTasksInProjectDTO> userTasks;


    public TaskResponse(String taskId) {
        this.taskId = taskId;
    }

    public TaskResponse(List<String> errors) {
        this.errors = errors;
    }

    public TaskResponse(TaskModel task) {
        this.task = task;
    }

    public TaskResponse(List<UsersTasksInProjectDTO> userTasks, SprintInfoDTO sprintInfo, List<UsersProjectsDTO> projects, String activeProject) {
        this.userTasks = userTasks;
        this.sprintInfo = sprintInfo;
        this.projects = projects;
        this.activeProject = activeProject;
    }
}
