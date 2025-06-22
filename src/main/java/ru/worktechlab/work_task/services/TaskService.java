package ru.worktechlab.work_task.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.request_dto.TaskModelDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.response.TaskResponse;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.task_comment.CommentDto;
import ru.worktechlab.work_task.dto.task_comment.CommentResponseDto;
import ru.worktechlab.work_task.models.enums.StatusName;
import ru.worktechlab.work_task.models.tables.Comment;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.*;
import ru.worktechlab.work_task.utils.UserContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UsersProjectsRepository usersProjectsRepository;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final TaskHistoryService taskHistorySaverService;
    private final UserService userService;
    private final CommentRepository commentRepository;

    @Transactional
    public TaskResponse updateTask(UpdateTaskModelDTO dto) {
        log.debug("Processing update-task with model: {}", dto);
        TaskModel existingTask = findTaskByIdOrThrow(dto.getId());
        taskHistorySaverService.saveTaskModelChanges(existingTask, dto);
        taskRepository.save(existingTask);

        log.info("Задача обновлена: id={}, title={}", existingTask.getId(), existingTask.getTitle());
        return new TaskResponse(existingTask.getId());
    }

    @TransactionRequired
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

    @TransactionRequired
    public TaskResponse createTask(TaskModelDTO taskDTO) {
        log.debug("Processing create-task with model: {}", taskDTO);
        TaskModel task = convertToEntity(taskDTO, userContext);
        taskRepository.save(task);
        return new TaskResponse(task.getId());
    }

    private TaskModel convertToEntity(TaskModelDTO taskDTO, UserContext userContext) {
        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(taskDTO.getTitle());
        taskModel.setDescription(taskDTO.getDescription());
        taskModel.setPriority(taskDTO.getPriority());
        taskModel.setAssignee(taskDTO.getAssignee());
        taskModel.setProjectId(taskDTO.getProjectId());
        taskModel.setSprintId(taskDTO.getSprintId());
        taskModel.setTaskType(taskDTO.getTaskType());
        taskModel.setEstimation(taskDTO.getEstimation());
        taskModel.setCreator(userContext.getUserData().getUserId());
        taskModel.setCode(getTaskCode(taskDTO.getProjectId()));
        taskModel.setCreationDate(LocalDateTime.now());
        taskModel.setStatus(StatusName.TODO.toString());
        return taskModel;
    }

    @TransactionMandatory
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

    public TaskModel findTaskByIdOrThrow(String id) {
        log.debug("Получение задачи по id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Задача с id: %s не найдена", id)));
    }

    @TransactionRequired
    public TaskModel updateTaskStatus(UpdateStatusRequestDTO requestDto) {
        log.debug("Обновить статус задачи");
        TaskModel task = findTaskByCodeOrThrow(requestDto.getCode());
        task.setStatus(requestDto.getStatus());
        taskHistorySaverService.saveTaskModelChanges(task, requestDto);

        return taskRepository.save(task);
    }

    private Comment convertToEntity(CommentDto dto, User user) {
        return new Comment(
                dto.getTaskId(),
                user,
                dto.getComment()
        );
    }

    @TransactionRequired
    public CommentResponseDto createComment(CommentDto dto) {
        User user = userService.findActiveUserById(userContext.getUserData().getUserId());
        Comment comment = convertToEntity(dto, user);
        commentRepository.save(comment);
        return new CommentResponseDto(comment.getId());
    }
}