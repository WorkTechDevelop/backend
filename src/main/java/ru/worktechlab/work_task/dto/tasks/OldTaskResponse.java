package ru.worktechlab.work_task.dto.tasks;

import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.sprints.SprintInfoDTO;
import ru.worktechlab.work_task.models.tables.TaskModel;

import java.util.List;

public class OldTaskResponse {
    private TaskModel task;
    private String taskId;
    private List<String> errors;
    private List<TaskModel> tasks;
    private SprintInfoDTO sprintInfo;
    private List<UsersProjectsDTO> projects;
    private String activeProject;
    private List<UsersTasksInProjectDTO> userTasks;


    public OldTaskResponse(String taskId) {
        this.taskId = taskId;
    }

    public OldTaskResponse(List<String> errors) {
        this.errors = errors;
    }

    public OldTaskResponse(TaskModel task) {
        this.task = task;
    }

    public OldTaskResponse(List<UsersTasksInProjectDTO> userTasks, SprintInfoDTO sprintInfo, List<UsersProjectsDTO> projects, String activeProject) {
        this.userTasks = userTasks;
        this.sprintInfo = sprintInfo;
        this.projects = projects;
        this.activeProject = activeProject;
    }
}
