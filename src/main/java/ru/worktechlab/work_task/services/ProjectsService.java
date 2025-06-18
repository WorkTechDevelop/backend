package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.OkResponse;
import ru.worktechlab.work_task.dto.StringIdsDto;
import ru.worktechlab.work_task.dto.projects.ProjectDto;
import ru.worktechlab.work_task.dto.projects.ProjectRequestDto;
import ru.worktechlab.work_task.dto.projects.ShortProjectDataDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.ProjectMapper;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.models.tables.*;
import ru.worktechlab.work_task.repositories.ProjectRepository;
import ru.worktechlab.work_task.repositories.SprintsRepository;
import ru.worktechlab.work_task.repositories.TaskStatusRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectsService {

    private static final String DEFAULT_SPRINT_NAME = "Список задач";

    private final UsersProjectsRepository usersProjectsRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final TaskStatusRepository taskStatusRepository;
    private final ProjectMapper projectMapper;
    private final SprintsRepository sprintsRepository;

    @TransactionRequired
    public List<ShortProjectDataDto> getAllUserProjects() {
        log.debug("Вывод всех проектов пользователя");
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        if (CollectionUtils.isEmpty(user.getProjects()))
            return Collections.emptyList();
        return projectMapper.toShortDataDto(user.getProjects());
    }

    @TransactionRequired
    public String getLastProjectId() {
        log.debug("Получить id активного проекта");
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        return user.getLastProjectId();
    }

    @TransactionMandatory
    public Project findProjectById(String projectId) throws NotFoundException {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден проект с ИД - %s", projectId))
        );
    }

    @TransactionMandatory
    public Project findProjectByIdForUpdate(String projectId) throws NotFoundException {
        return projectRepository.findProjectByIdForUpdate(projectId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден проект с ИД - %s", projectId))
        );
    }

    @TransactionRequired
    public ProjectDto createProject(ProjectRequestDto data) {
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        Project project = new Project(data.getName(), user, data.getDescription(), data.isActive(), user, data.getCode());
        projectRepository.saveAndFlush(project);
        createDefaultStatuses(project);
        createDefaultSprint(user, project);
        usersProjectsRepository.saveAndFlush(new UsersProject(userId, project.getId()));
        return projectMapper.toProjectDto(project);
    }

    @TransactionMandatory
    public void createDefaultSprint(User user,
                                    Project project) {
        sprintsRepository.saveAndFlush(new Sprint(DEFAULT_SPRINT_NAME, user, project));
    }

    @TransactionMandatory
    public void createDefaultStatuses(Project project) {
        taskStatusRepository.saveAllAndFlush(Arrays.stream(StatusName.values())
                .map(status -> new TaskStatus(
                        status.getPriority(), status.name(), status.getDescription(), status.isViewed(), status.isDefaultTaskStatus(), project)
                )
                .toList());
    }

    @TransactionRequired
    public ProjectDto getProjectData(String projectId) throws NotFoundException {
        Project project = findProjectById(projectId);
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        userService.checkHasProjectForUser(user, projectId);
        user.setLastProjectId(projectId);
        return projectMapper.toProjectDto(project);
    }

    @TransactionRequired
    public ProjectDto finishProject(String projectId) throws NotFoundException {
        Project project = findProjectByIdForUpdate(projectId);
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        userService.checkHasProjectForUser(user, projectId);
        project.finishProject(user);
        projectRepository.flush();
        project = findProjectById(projectId);
        return projectMapper.toProjectDto(project);
    }

    @TransactionRequired
    public ProjectDto startProject(String projectId) throws NotFoundException {
        Project project = findProjectByIdForUpdate(projectId);
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        userService.checkHasProjectForUser(user, projectId);
        project.startProject();
        projectRepository.flush();
        project = findProjectById(projectId);
        return projectMapper.toProjectDto(project);
    }

    private boolean hasProject(User user, String projectId) {
        return user.getProjects().stream()
                .anyMatch(project -> Objects.equals(project.getId(), projectId));
    }

    @TransactionRequired
    public OkResponse addProjectForUsers(String projectId,
                                         StringIdsDto data) throws NotFoundException {
        OkResponse response = new OkResponse();
        if (data == null || org.springframework.util.CollectionUtils.isEmpty(data.getIds()))
            return response;
        Project project = findProjectByIdForUpdate(projectId);
        List<User> users = userService.findAndCheckUsers(data.getIds());
        usersProjectsRepository.saveAllAndFlush(users.stream()
                .filter(user -> !hasProject(user, project.getId()))
                .map(user -> new UsersProject(user.getId(), project.getId()))
                .toList());
        return response;
    }
}
