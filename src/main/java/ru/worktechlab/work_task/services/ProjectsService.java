package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
@RequiredArgsConstructor
@Slf4j
public class ProjectsService {
    private final UserRepository userRepository;
    private final UsersProjectsRepository usersProjectsRepository;
    private final TaskService taskService;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserContext userContext;


    public List<UsersProjectsDTO> getAllUserProjects() {
        log.debug("Вывод всех проектов пользователя");
        String userId = userContext.getUserData().getUserId();
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(userId);
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyList();
        }
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public List<TaskModel> setMainProject(String projectId) {
        log.debug("Установить проект основным {}", projectId);
        String userId = userContext.getUserData().getUserId();
        userRepository.updateLastProjectIdById(userId, projectId);
        return taskService.getTasksByProjectId(projectId);
    }

    public List<UsersProjectsDTO> getUserProject(User user) {
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(user.getId());
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public List<UsersProjectsDTO> getUserProject() {
        log.debug("Вывод всех проектов пользователя");
        String userId = userContext.getUserData().getUserId();
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(userId);
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public String getLastProjectId() {
        log.debug("Получить id активного проекта");
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
        return user.getLastProjectId();
    }
}
