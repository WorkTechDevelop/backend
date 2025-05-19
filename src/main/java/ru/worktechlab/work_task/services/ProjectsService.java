package ru.worktechlab.work_task.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.authorization.jwt.JwtUtils;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.ProjectRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;
import ru.worktechlab.work_task.repositories.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class ProjectsService {
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private UsersProjectsRepository usersProjectsRepository;
    private TaskService taskService;
    private ProjectRepository projectRepository;
    private final TokenService tokenService;


    public List<UsersProjectsDTO> getAllUserProjects(String jwtToken) {
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(
                jwtUtils.getUserGuidFromJwtToken(jwtToken)
        );
        if (projectIds.isEmpty()) {
            return Collections.emptyList();
        }
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public List<TaskModel> setMainProject(String projectId, String jwtToken) {
        userRepository.updateLastProjectIdById(jwtUtils.getUserGuidFromJwtToken(jwtToken), projectId);
        return taskService.getTasksByProjectId(projectId);
    }

    public List<UsersProjectsDTO> getUserProject(User user) {
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(user.getId());
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public List<UsersProjectsDTO> getUserProject(String jwtToken) {
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(tokenService.getUserGuidFromJwtToken(jwtToken));
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public String getLastProjectId(String jwtToken) {
        String userId = tokenService.getUserGuidFromJwtToken(jwtToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", userId)));
        return user.getLastProjectId();
    }
}
