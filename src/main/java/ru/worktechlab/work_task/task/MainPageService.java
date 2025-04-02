package ru.worktechlab.work_task.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.projects.ProjectsService;
import ru.worktechlab.work_task.responseDTO.SprintInfoDTO;
import ru.worktechlab.work_task.responseDTO.UsersProjectsDTO;
import ru.worktechlab.work_task.responseDTO.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.sprints.SprintsService;
import ru.worktechlab.work_task.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final TaskService taskService;
    private final SprintsService sprintsService;
    private final ProjectsService projectsService;
    private final UserService userService;

    public TaskResponse getMainPageData(String jwtToken) {
        Users user = userService.getUser(jwtToken);
        List<UsersTasksInProjectDTO> usersTasks = taskService.getProjectTaskByUserGuid(user);
        SprintInfoDTO sprintInfo = sprintsService.getSprintName(user);
        List<UsersProjectsDTO> projects = projectsService.getUserProject(user);
        String activeProject = user.getLastProjectId();

        return new TaskResponse(usersTasks, sprintInfo, projects, activeProject);
    }
}