package ru.worktechlab.work_task.responseDTO;

import lombok.Data;

@Data
public class UsersProjectsDTO {
    private String projectName;
    private String projectId;

    public UsersProjectsDTO(String projectName, String projectId) {
        this.projectName = projectName;
        this.projectId = projectId;
    }
}
