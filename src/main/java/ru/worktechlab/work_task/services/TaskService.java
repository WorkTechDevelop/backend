package ru.worktechlab.work_task.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.dto.request_dto.TaskModelDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.response.TaskResponse;
import ru.worktechlab.work_task.dto.response_dto.TaskModeResponselDTO;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.mappers.TaskModelMapper;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.ProjectRepository;
import ru.worktechlab.work_task.repositories.TaskRepository;
import ru.worktechlab.work_task.repositories.UserRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.utils.TaskModelConverter;
import ru.worktechlab.work_task.utils.UserContext;
import ru.worktechlab.work_task.validators.ProjectValidator;
import ru.worktechlab.work_task.validators.TaskValidator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskModelMapper taskModelMapper;
    private final TaskValidator taskValidator;
    private final ProjectValidator projectValidator;
    private final UserRepository userRepository;
    private final UsersProjectsRepository usersProjectsRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TaskModelConverter taskModelConverter;
    private final UserContext userContext;
    private final TaskHistorySaverService taskHistorySaverService;

    @Transactional
    public TaskResponse updateTask(UpdateTaskModelDTO dto) {
        log.debug("Processing update-task with model: {}", dto);
        TaskModel existingTask = findTaskByCodeOrThrow(dto.getCode());
        TaskModel oldTask = TaskModel.copyOf(existingTask);
        taskModelMapper.updateTaskFromDto(dto, existingTask);
        taskHistorySaverService.saveTaskModelChanges(oldTask, existingTask);

        taskRepository.save(existingTask);

        log.info("Задача обновлена: id={}, title={}", existingTask.getId(), existingTask.getTitle());
        return new TaskResponse(existingTask.getId());
    }

    public List<TaskModel> getTasksByProjectId(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<TaskModel> getTasksByProjectIdOrThrow(String projectId) {
        projectValidator.validateProjectExist(projectId);
        List<TaskModel> tasks = taskRepository.findByProjectId(projectId);
        taskValidator.validateTasksExist(tasks, projectId);

        return tasks;
    }

    public List<TaskModeResponselDTO> getTasksModelResponseByProjectIdOrThrow(String projectId) {
        validateProjectAndTasks(projectId);
        List<TaskModel> tasks = taskRepository.findByProjectId(projectId);
        Map<String, User> usersById = preloadUsers(tasks);
        return taskModelConverter.convertTasks(tasks, usersById);
    }

    private void validateProjectAndTasks(String projectId) {
        projectValidator.validateProjectExist(projectId);
        List<TaskModel> tasks = taskRepository.findByProjectId(projectId);
        taskValidator.validateTasksExist(tasks, projectId);
    }

    private Map<String, User> preloadUsers(List<TaskModel> tasks) {
        Set<String> userIds = tasks.stream()
                .flatMap(task -> Stream.of(task.getCreator(), task.getAssignee()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return userService.findAllByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    public List<UsersTasksInProjectDTO> getProjectTaskByUserGuid() {
        log.debug("Вывод всех задач проекта отсартированных по пользователям");
        String userId = userContext.getUserData().getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", userId)));
        List<String> userIds = usersProjectsRepository.findUserByProjectId(user.getLastProjectId());
        List<UsersTasksInProjectDTO> userTasks = taskRepository.findUserTasksByUserIds(userIds);
        Map<String, List<TaskModel>> tasksByUser = userTasks.stream()
                .collect(Collectors.groupingBy(
                        UsersTasksInProjectDTO::getUserName,
                        Collectors.flatMapping(
                                dto -> dto.getTasks().stream(),
                                Collectors.toList()
                        )
                ));

        return tasksByUser.entrySet().stream()
                .map(entry -> new UsersTasksInProjectDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<UsersTasksInProjectDTO> fetchUserTasks(List<String> userIds) {
        return taskRepository.findUserTasksByUserIds(userIds);
    }

    private List<UsersTasksInProjectDTO> groupTasksByUser(List<UsersTasksInProjectDTO> userTasks) {
        Map<String, List<TaskModel>> tasksByUser = userTasks.stream()
                .collect(Collectors.groupingBy(
                        UsersTasksInProjectDTO::getUserName,
                        Collectors.flatMapping(
                                dto -> dto.getTasks().stream(),
                                Collectors.toList()
                        )
                ));

        return tasksByUser.entrySet().stream()
                .map(entry -> new UsersTasksInProjectDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public TaskResponse createTask(TaskModelDTO taskDTO) {
        log.debug("Processing create-task with model: {}", taskDTO);
        TaskModel task = taskModelMapper.toEntity(taskDTO, userContext.getUserData().getUserId(),
                getTaskCode(taskDTO.getProjectId()));
        taskRepository.save(task);
        return new TaskResponse(task.getId());
    }

    public String getTaskCode(String projectId) {
        String code = projectRepository.getCodeById(projectId);
        projectRepository.incrementCount(projectId);
        Integer count = projectRepository.getCountById(projectId);
        return code + "-" + count;
    }

    public TaskModel findTaskByCodeOrThrow(String taskCode) {
        log.debug("Получение задачи по коду: {}", taskCode);
        return taskRepository.findByCode(taskCode)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Задача с кодом: %s не найдена", taskCode)));
    }

    @Transactional
    public TaskModel updateTaskStatus(UpdateStatusRequestDTO requestDto) {
        log.debug("Обновить статус задачи");
        TaskModel task = findTaskByCodeOrThrow(requestDto.getCode());
        TaskModel oldTask = TaskModel.copyOf(task);
        task.setStatus(requestDto.getStatus());
        taskHistorySaverService.saveTaskModelStatusChanges(oldTask, task);

        return taskRepository.save(task);
    }
}