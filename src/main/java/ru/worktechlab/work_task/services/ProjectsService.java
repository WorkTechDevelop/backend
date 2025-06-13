package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.projects.ProjectDto;
import ru.worktechlab.work_task.dto.projects.ProjectRequestDto;
import ru.worktechlab.work_task.dto.projects.ShortProjectDataDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.ProjectMapper;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.ProjectStatus;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.models.tables.UsersProject;
import ru.worktechlab.work_task.repositories.ProjectRepository;
import ru.worktechlab.work_task.repositories.ProjectStatusRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectsService {
    private final UsersProjectsRepository usersProjectsRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectMapper projectMapper;

    @TransactionRequired
    public List<ShortProjectDataDto> getAllUserProjects() {
        log.debug("Вывод всех проектов пользователя");
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
        if (CollectionUtils.isEmpty(user.getProjects()))
            return Collections.emptyList();
        return projectMapper.toShortDataDto(user.getProjects());
    }

    @TransactionRequired
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

    @TransactionMandatory
    public Project findProjectByIdForUpdate(String projectId) throws NotFoundException {
        return projectRepository.findProjectByForUpdate(projectId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден проект с ИД - %s", projectId))
        );
    }

    @TransactionRequired
    public ProjectDto createProject(ProjectRequestDto data) {
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
        Project project = new Project(data.getName(), user, data.getDescription(), data.isActive(), user, data.getCode());
        projectRepository.saveAndFlush(project);
        createDefaultStatuses(project);
        usersProjectsRepository.saveAndFlush(new UsersProject(userId, project.getId()));
        return projectMapper.toProjectDto(project);
    }

    @TransactionMandatory
    public void createDefaultStatuses(Project project) {
        projectStatusRepository.saveAllAndFlush(Arrays.stream(StatusName.values())
                .map(status -> new ProjectStatus(
                        status.getPriority(), status.name(), status.getDescription(), status.isViewed(), project)
                )
                .toList());
    }

    @TransactionRequired
    public ProjectDto getProjectData(String projectId) throws NotFoundException {
        Project project = findProjectById(projectId);
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
        userService.checkHasProjectForUser(user, projectId);
        user.setLastProjectId(projectId);
        return projectMapper.toProjectDto(project);
    }

    @TransactionRequired
    public ProjectDto finishProject(String projectId) throws NotFoundException {
        Project project = findProjectByIdForUpdate(projectId);
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
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
        User user = userService.findUserById(userId);
        userService.checkHasProjectForUser(user, projectId);
        project.startProject();
        projectRepository.flush();
        project = findProjectById(projectId);
        return projectMapper.toProjectDto(project);
    }
}
