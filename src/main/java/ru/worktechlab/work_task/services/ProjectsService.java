package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.StringIdsDto;
import ru.worktechlab.work_task.dto.UserAndProjectData;
import ru.worktechlab.work_task.dto.projects.*;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.exceptions.BadRequestException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.ProjectMapper;
import ru.worktechlab.work_task.mappers.TaskMapper;
import ru.worktechlab.work_task.models.enums.Roles;
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
    private final RoleService roleService;

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
    public ProjectDto finishProject(String projectId) throws NotFoundException, BadRequestException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, true, false);
        checkerUtil.checkProjectOwner(data.getProject(), data.getUser());
        data.getProject().finishProject(data.getUser());
        projectRepository.flush();
        Project project = findProjectById(projectId);
        return projectMapper.toProjectDto(project);
    }

    @TransactionRequired
    public ProjectDto startProject(String projectId) throws NotFoundException, BadRequestException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, true, false);
        checkerUtil.checkProjectOwner(data.getProject(), data.getUser());
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
    public void addProjectForUsers(String projectId,
                                   StringIdsDto request) throws NotFoundException, BadRequestException {
        if (request == null || CollectionUtils.isEmpty(request.getIds()))
            return;
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        Project project = data.getProject();
        checkerUtil.checkProjectOwner(project, data.getUser());
        List<User> users = userService.findAndCheckUsers(request.getIds());
        usersProjectsRepository.saveAllAndFlush(users.stream()
                .filter(user -> !hasProject(user, project.getId()))
                .map(user -> new UsersProject(user, project))
                .toList());
    }

    @TransactionRequired
    public void deleteProjectForUsers(String projectId,
                                      StringIdsDto request) throws NotFoundException, BadRequestException {
        if (request == null || CollectionUtils.isEmpty(request.getIds()))
            return;
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        checkerUtil.checkProjectOwner(data.getProject(), data.getUser());
        userService.findAndCheckUsers(request.getIds());
        usersProjectsRepository.deleteProjectForUsers(projectId, request.getIds());
        usersProjectsRepository.flush();
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

    @TransactionRequired
    public void addProjectOwner(String projectId,
                                String userId) throws NotFoundException {
        Project project = findProjectByIdForUpdate(projectId);
        User owner = userService.findActiveUserById(userId);
        project.setProjectOwner(owner);
        roleService.addUserRoles(owner, Roles.PROJECT_OWNER);
        if (!hasProject(owner, projectId))
            usersProjectsRepository.save(new UsersProject(owner, project));
        projectRepository.flush();
    }

    @TransactionRequired
    public void addExtendedPermissionsForUserProject(String projectId,
                                                     String userId) throws NotFoundException, BadRequestException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        checkerUtil.checkProjectOwner(data.getProject(), data.getUser());
        User userForUpdatePermissions = userService.findActiveUserById(userId);
        roleService.addUserRoles(userForUpdatePermissions, Roles.POWER_USER);
        roleService.addUserExtendedPermissions(userForUpdatePermissions, data.getProject());
        if (!hasProject(userForUpdatePermissions, projectId))
            usersProjectsRepository.save(new UsersProject(userForUpdatePermissions, data.getProject()));
        projectRepository.flush();
    }

    @TransactionRequired
    public void deleteExtendedPermissionsForUserProject(String projectId,
                                                        String userId) throws NotFoundException, BadRequestException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        checkerUtil.checkProjectOwner(data.getProject(), data.getUser());
        User userForUpdatePermissions = userService.findActiveUserById(userId);
        roleService.deleteUserExtendedPermissions(userForUpdatePermissions, data.getProject());
        projectRepository.flush();
    }
}
