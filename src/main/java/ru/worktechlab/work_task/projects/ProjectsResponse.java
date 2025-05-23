package ru.worktechlab.work_task.projects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.worktechlab.work_task.model.db.Projects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ProjectsResponse {
    private Projects projects;
    private String projectId;

    public ProjectsResponse(String projectId) {
        this.projectId = projectId;
    }
}
