package ru.worktechlab.work_task.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.UserAndProjectData;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.task_comment.CommentDto;
import ru.worktechlab.work_task.dto.task_comment.CommentResponseDto;
import ru.worktechlab.work_task.dto.task_comment.UpdateCommentDto;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.dto.tasks.TaskModelDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateTaskModelDTO;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.exceptions.PermissionDeniedException;
import ru.worktechlab.work_task.mappers.CommentMapper;
import ru.worktechlab.work_task.mappers.TaskMapper;
import ru.worktechlab.work_task.models.tables.*;
import ru.worktechlab.work_task.repositories.CommentRepository;
import ru.worktechlab.work_task.repositories.TaskRepository;
import ru.worktechlab.work_task.repositories.UserRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.utils.CheckerUtil;
import ru.worktechlab.work_task.utils.UserContext;

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
    private final UserContext userContext;
    private final TaskHistoryService taskHistorySaverService;
    private final SprintsService sprintsService;
    private final TaskMapper taskMapper;
    private final CheckerUtil checkerUtil;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public TaskDataDto updateTask(UpdateTaskModelDTO dto) throws NotFoundException {
        log.debug("Processing update-task with model: {}", dto);
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        TaskModel existingTask = findTaskByIdAndProjectForUpdate(dto.getId(), data.getProject());
        taskHistorySaverService.saveTaskModelChanges(existingTask, dto, data.getProject(), data.getUser());
        taskRepository.flush();
        log.debug("Задача обновлена: id={}, title={}", existingTask.getId(), existingTask.getTitle());
        return taskMapper.toDo(findTaskByIdOrThrow(existingTask.getId()));
    }

    @TransactionRequired
    public List<UsersTasksInProjectDTO> getProjectTaskByUserGuid() {
        log.debug("Вывод всех задач проекта отсортированных по пользователям");
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
                .toList();
    }

    @TransactionRequired
    public TaskDataDto createTask(TaskModelDTO taskDTO) throws NotFoundException {
        log.debug("Processing create-task with model: {}", taskDTO);
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(taskDTO.getProjectId(), false, false);
        TaskModel task = convertToEntity(taskDTO, data.getUser(), data.getProject());
        taskRepository.saveAndFlush(task);
        return taskMapper.toDo(task);
    }

    private TaskModel convertToEntity(TaskModelDTO taskDTO,
                                      User user,
                                      Project project) throws NotFoundException {
        User assignee = null;
        if (taskDTO.getAssignee() != null)
            assignee = checkerUtil.findAndCheckActiveUser(taskDTO.getAssignee(), project);
        Sprint sprint = sprintsService.findSprintByIdAndProject(taskDTO.getSprintId(), project);
        TaskStatus status = findDefaultStatus(project);
        return new TaskModel(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getPriority(), user, assignee, project,
                sprint, taskDTO.getTaskType(), taskDTO.getEstimation(), status, getTaskCode(project));
    }

    private TaskStatus findDefaultStatus(Project project) throws NotFoundException {
        return project.getStatuses().stream()
                .filter(TaskStatus::isDefaultTaskStatus)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Не найден дефолтный статус для проекта %s", project.getName())));
    }

    private String getTaskCode(Project project) {
        project.incrementCounter();
        return project.getCode() + "-" + project.getTaskCounter();
    }

    public TaskModel findTaskByIdOrThrow(String id) {
        log.debug("Получение задачи по id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Задача с id: %s не найдена", id)));
    }

    @TransactionMandatory
    public TaskModel findTaskByIdAndProject(String taskId,
                                            Project project) throws NotFoundException {
        return taskRepository.findTaskModelByIdAndProject(taskId, project)
                .orElseThrow(() -> new NotFoundException(String.format("Для проекта %s не найдена задача с ид - %s", project.getName(), taskId)));
    }

    @TransactionMandatory
    public TaskModel findTaskByIdAndProjectForUpdate(String taskId,
                                                     Project project) throws NotFoundException {
        return taskRepository.findTaskModelByIdAndProjectForUpdate(taskId, project.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Для проекта %s не найдена задача с ид - %s", project.getName(), taskId)));
    }

    @TransactionRequired
    public TaskDataDto updateTaskStatus(UpdateStatusRequestDTO requestDto) throws NotFoundException {
        log.debug("Обновить статус задачи");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(requestDto.getProjectId(), false, false);
        TaskModel task = findTaskByIdAndProjectForUpdate(requestDto.getId(), data.getProject());
        taskHistorySaverService.saveTaskModelChanges(task, requestDto, data.getProject(), data.getUser());
        taskRepository.flush();
        return taskMapper.toDo(findTaskByIdOrThrow(task.getId()));
    }

    private Comment convertToEntity(CommentDto dto, User user) {
        return new Comment(
                dto.getTaskId(),
                user,
                dto.getComment()
        );
    }

    public Comment findCommentForUpdateByIdOrElseThrow(String id) {
        return commentRepository.findCommentByIdForUpdate(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден комментарий с таким id - %s", id)
                ));
    }

    @TransactionRequired
    public CommentResponseDto createComment(CommentDto dto) throws NotFoundException {
        log.debug("Создать комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        Comment comment = convertToEntity(dto, data.getUser());
        commentRepository.saveAndFlush(comment);
        return commentMapper.toDto(comment);
    }

    @TransactionRequired
    public CommentResponseDto updateComment(UpdateCommentDto dto) throws NotFoundException {
        log.debug("Обновить комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        Comment comment = findCommentForUpdateByIdOrElseThrow(dto.getCommentId());
        User creator = comment.getUser();
        User modifier = data.getUser();
        if (!userService.compareUsers(creator, modifier)) {
            throw new PermissionDeniedException(
                    "Только создатель комментария имеет право редактирования");
        }
        taskHistorySaverService.saveTaskCommentChanges(comment, dto, modifier);
        commentRepository.flush();
        log.debug("Комментарий обновлен: id={}", comment.getId());
        return commentMapper.toDto(comment);
    }
}