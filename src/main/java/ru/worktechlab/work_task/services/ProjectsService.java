package ru.worktechlab.work_task.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.ProjectRepository;
import ru.worktechlab.work_task.repositories.UserRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectsService {
    private UserRepository userRepository;
    private UsersProjectsRepository usersProjectsRepository;
    private TaskService taskService;
    private ProjectRepository projectRepository;
    private final UserService userService;
    private final UserContext userContext;


    public List<UsersProjectsDTO> getAllUserProjects() {
        String userId = userContext.getUserData().getUserId();
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(userId);
        if (projectIds.isEmpty()) {
            return Collections.emptyList();
        }
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public List<TaskModel> setMainProject(String projectId) {
        String userId = userContext.getUserData().getUserId();
        userRepository.updateLastProjectIdById(userId, projectId);
        return taskService.getTasksByProjectId(projectId);
    }

    public List<UsersProjectsDTO> getUserProject(User user) {
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(user.getId());
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public List<UsersProjectsDTO> getUserProject() {
        String userId = userContext.getUserData().getUserId();
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(userId);
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public String getLastProjectId() {
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
        return user.getLastProjectId();
    }
}
