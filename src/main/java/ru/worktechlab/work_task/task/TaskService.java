package ru.worktechlab.work_task.task;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.jwt.TokenService;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.mappers.TaskModelMapper;
import ru.worktechlab.work_task.model.rest.TaskModelDTO;
import ru.worktechlab.work_task.model.rest.UpdateStatusRequestDto;
import ru.worktechlab.work_task.model.rest.UpdateTaskModelDTO;
import ru.worktechlab.work_task.projects.UsersProjectsRepository;
import ru.worktechlab.work_task.responseDTO.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.task.validators.ProjectValidator;
import ru.worktechlab.work_task.task.validators.TaskValidator;
import ru.worktechlab.work_task.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private final TaskModelMapper taskModelMapper;
    private final TokenService tokenService;
    private final TaskValidator taskValidator;
    private final ProjectValidator projectValidator;
    private final UserRepository userRepository;
    private final UsersProjectsRepository usersProjectsRepository;

    @Transactional
    public TaskResponse updateTask(UpdateTaskModelDTO dto) {
        TaskModel existingTask = findTaskByIdOrThrow(dto.getId());
        taskModelMapper.updateTaskFromDto(dto, existingTask);
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

    public List<UsersTasksInProjectDTO> getProjectTaskByUserGuid(String jwtToken) {
        String userId = tokenService.getUserGuidFromJwtToken(jwtToken);
        Users user = userRepository.findById(userId)
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

    public TaskResponse createTask(TaskModelDTO taskDTO, String jwtToken) {
        TaskModel task = taskModelMapper.toEntity(taskDTO, tokenService.getUserGuidFromJwtToken(jwtToken));
        taskRepository.save(task);
        return new TaskResponse(task.getId());
    }

    public TaskModel findTaskByIdOrThrow(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Задача с id: %s не найдена", taskId)));
    }

    @Transactional
    public TaskModel updateTaskStatus(UpdateStatusRequestDto requestDto) {
        TaskModel task = findTaskByIdOrThrow(requestDto.getId());
        task.setStatus(requestDto.getStatus());
        return taskRepository.save(task);
    }
}