package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.OkResponse;
import ru.worktechlab.work_task.dto.StringIdsDto;
import ru.worktechlab.work_task.dto.UserAndProjectData;
import ru.worktechlab.work_task.dto.projects.*;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.ProjectMapper;
import ru.worktechlab.work_task.mappers.TaskMapper;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.models.tables.*;
import ru.worktechlab.work_task.repositories.*;
import ru.worktechlab.work_task.utils.CheckerUtil;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.*;
import java.util.stream.Collectors;

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
    private final CheckerUtil checkerUtil;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

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
        usersProjectsRepository.saveAndFlush(new UsersProject(user, project));
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
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, true);
        User user = data.getUser();
        user.setLastProjectId(projectId);
        projectRepository.flush();
        return projectMapper.toProjectDto(data.getProject());
    }

    @TransactionRequired
    public ProjectDto finishProject(String projectId) throws NotFoundException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, true, false);
        data.getProject().finishProject(data.getUser());
        projectRepository.flush();
        Project project = findProjectById(projectId);
        return projectMapper.toProjectDto(project);
    }

    @TransactionRequired
    public ProjectDto startProject(String projectId) throws NotFoundException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, true, false);
        data.getProject().startProject();
        projectRepository.flush();
        Project project = findProjectById(projectId);
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
        Project project = findProjectById(projectId);
        List<User> users = userService.findAndCheckUsers(data.getIds());
        usersProjectsRepository.saveAllAndFlush(users.stream()
                .filter(user -> !hasProject(user, project.getId()))
                .map(user -> new UsersProject(user, project))
                .toList());
        return response;
    }

    @TransactionRequired
    public OkResponse deleteProjectForUsers(String projectId,
                                            StringIdsDto data) throws NotFoundException {
        OkResponse response = new OkResponse();
        if (data == null || CollectionUtils.isEmpty(data.getIds()))
            return response;
        findProjectById(projectId);
        userService.findAndCheckUsers(data.getIds());
        usersProjectsRepository.deleteProjectForUsers(projectId, data.getIds());
        usersProjectsRepository.flush();
        return response;
    }

    @TransactionRequired
    public ProjectDataDto getProjectDataByFilter(String projectId,
                                     ProjectDataFilterDto filter) throws NotFoundException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, true);
        User user = data.getUser();
        user.setLastProjectId(projectId);
        projectRepository.flush();
        List<User> users = userRepository.findProjectUsers(filter.getUserIds(), data.getProject());
        Map<String, List<TaskModel>> tasksByUserId = taskRepository.findTaskByUsers(data.getProject(), users, filter.getStatusIds()).stream()
                .collect(Collectors.groupingBy(task -> task.getAssignee().getId()));
        List<UserWithTasksDto> userData = users.stream()
                .map(u -> toUserWithTasks(u, tasksByUserId.get(u.getId())))
                .toList();
        ProjectDataDto response = new ProjectDataDto();
        response.setUsers(userData);
        return response;
    }

    private UserWithTasksDto toUserWithTasks(User user,
                                             List<TaskModel> dbTasks) {
        List<TaskDataDto> tasks = CollectionUtils.isEmpty(dbTasks) ? Collections.emptyList()
                : dbTasks.stream()
                .map(taskMapper::toDo)
                .toList();
        return new UserWithTasksDto(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getGender().name(), tasks);
    }
}
