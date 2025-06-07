package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.projects.ProjectRequestDto;
import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.models.tables.*;
import ru.worktechlab.work_task.repositories.ProjectRepository;
import ru.worktechlab.work_task.repositories.ProjectStatusRepository;
import ru.worktechlab.work_task.repositories.UserRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.Arrays;
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
    private final ProjectStatusRepository projectStatusRepository;


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

    @TransactionMandatory
    public Project findProjectById(String projectId) throws NotFoundException {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден проект с ИД - %s", projectId))
        );
    }

    @TransactionRequired
    public String createProject(ProjectRequestDto data) {
        String userId = userContext.getUserData().getUserId();
        Project project = new Project(data.getName(), userId, data.getDescription(), data.isActive(), userId, data.getCode());
        projectRepository.saveAndFlush(project);
        createDefaultStatuses(project);
        usersProjectsRepository.saveAndFlush(new UsersProject(userId, project.getId()));
        return "Проект успешно создан";
    }

    @TransactionMandatory
    public void createDefaultStatuses(Project project) {
        projectStatusRepository.saveAllAndFlush(Arrays.stream(StatusName.values())
                .map(status -> new ProjectStatus(
                        status.getPriority(), status.name(), status.getDescription(), status.isViewed(), project)
                )
                .toList());
    }
}
