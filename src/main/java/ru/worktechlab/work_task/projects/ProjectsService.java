package ru.worktechlab.work_task.projects;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.task.TaskService;
import ru.worktechlab.work_task.user.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectsService {
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private UsersProjectsRepository usersProjectsRepository;
    private TaskService taskService;


    public List<String> getAllUserProjects(String jwtToken) {
        return usersProjectsRepository.findProjectsByUserId(jwtUtils.getUserGuidFromJwtToken(jwtToken));
    }

    public List<TaskModel> setMainProject(String projectId, String jwtToken) {
        userRepository.updateLastProjectIdById(jwtUtils.getUserGuidFromJwtToken(jwtToken), projectId);
        return taskService.getTasksByProjectId(projectId);
    }
}
