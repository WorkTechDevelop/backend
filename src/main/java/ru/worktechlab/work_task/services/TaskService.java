package ru.worktechlab.work_task.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.ApiResponse;
import ru.worktechlab.work_task.dto.UserAndProjectData;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;
import ru.worktechlab.work_task.dto.task_comment.AllTasksCommentsResponseDto;
import ru.worktechlab.work_task.dto.task_comment.CommentDto;
import ru.worktechlab.work_task.dto.task_comment.CommentResponseDto;
import ru.worktechlab.work_task.dto.task_comment.UpdateCommentDto;
import ru.worktechlab.work_task.dto.task_link.LinkDto;
import ru.worktechlab.work_task.dto.task_link.LinkResponseDto;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.dto.tasks.TaskModelDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateTaskModelDTO;
import ru.worktechlab.work_task.exceptions.DuplicateLinkException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.CommentMapper;
import ru.worktechlab.work_task.mappers.LinkMapper;
import ru.worktechlab.work_task.mappers.TaskMapper;
import ru.worktechlab.work_task.models.enums.LinkTypeName;
import ru.worktechlab.work_task.models.tables.*;
import ru.worktechlab.work_task.repositories.*;
import ru.worktechlab.work_task.utils.CheckerUtil;
import ru.worktechlab.work_task.utils.NormalizedLinkData;
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
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    @TransactionRequired
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

    private Comment convertToEntity(CommentDto dto, User user, TaskModel task) {
        return new Comment(
                task,
                user,
                dto.getComment()
        );
    }

    private Link convertToEntity(NormalizedLinkData data) {
        return new Link(
                data.master(),
                data.slave(),
                data.linkTypeName()
        );
    }

    @TransactionRequired
    public Comment findCommentForUpdateByIdOrElseThrow(String id, String taskId) {
        return commentRepository.findCommentByIdForUpdate(id, taskId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден комментарий с таким id - %s для задачи - %s", id, taskId)
                ));
    }

    @TransactionRequired
    public Comment findCommentByIdOrElseThrow(String id, String taskId) {
        return commentRepository.findByCommentAndTaskAds(id, taskId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден комментарий с таким id - %s для задачи - %s", id, taskId)
                ));
    }

    private NormalizedLinkData normalizedLink(TaskModel source, TaskModel target, String linkTypeName) {
        LinkTypeName typeName = LinkTypeName.valueOf(linkTypeName);
        LinkTypeName canonical = typeName.getCanonical();
        if (typeName != canonical && typeName.inverseExist()) {
            LinkTypeName canonicalLinkType = typeName.getInverse();
            return new NormalizedLinkData(target, source, canonicalLinkType);
        }
        return new NormalizedLinkData(source, target, typeName);
    }

    private void checkHasTasksLinkExist(NormalizedLinkData data) {
        linkRepository.findByTasksLinkedAndType(data.master().getId(), data.slave().getId(), data.linkTypeName())
                .ifPresent(existing -> {
                    throw new DuplicateLinkException("Связь между задачами уже существует");
                });
    }

    @TransactionRequired
    public CommentResponseDto createComment(CommentDto dto) throws NotFoundException {
        log.debug("Создать комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        TaskModel task = findTaskByIdAndProject(dto.getTaskId(), data.getProject());
        Comment comment = convertToEntity(dto, data.getUser(), task);
        commentRepository.saveAndFlush(comment);
        return commentMapper.toDto(comment);
    }

    @TransactionRequired
    public CommentResponseDto updateComment(UpdateCommentDto dto) throws NotFoundException {
        log.debug("Обновить комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        TaskModel task = findTaskByIdAndProject(dto.getTaskId(), data.getProject());
        Comment comment = findCommentForUpdateByIdOrElseThrow(dto.getCommentId(), task.getId());
        taskHistorySaverService.saveTaskCommentChanges(comment, dto, data.getUser(), task);
        commentRepository.flush();
        log.debug("Комментарий обновлен: id={}", comment.getId());
        return commentMapper.toDto(comment);
    }

    @TransactionRequired
    public ApiResponse deleteComment(String commentId, String taskId, String projectId) throws NotFoundException {
        log.debug("Удалить комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        TaskModel task = findTaskByIdAndProject(taskId, data.getProject());
        findCommentByIdOrElseThrow(commentId, task.getId());
        commentRepository.deleteCommentById(commentId);
        commentRepository.flush();
        ApiResponse apiResponse = new ApiResponse("Комментарий успешно удалён");
        log.info("Пользователь {} удалил комментарий {}", data.getUser().getFirstName() + " " + data.getUser().getLastName(), commentId);
        return apiResponse;
    }

    @TransactionRequired
    public List<AllTasksCommentsResponseDto> allTasksComments(String taskId, String projectId) throws NotFoundException {
        log.debug("Получить все комментарии к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        findTaskByIdAndProject(taskId, data.getProject());
        List<Comment> comments = commentRepository.findAllByTaskIdOrderByCreatedAtAsc(taskId);
        return commentMapper.toGetAllDtoList(comments);
    }

    @TransactionRequired
    public LinkResponseDto linkTask(LinkDto dto) throws NotFoundException {
        log.debug("Создать связь мкжду задачами");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        Project project = data.getProject();
        TaskModel source = findTaskByIdAndProject(dto.getTaskIdSource(), project);
        TaskModel target = findTaskByIdAndProject(dto.getTaskIdTarget(), project);
        NormalizedLinkData linkData = normalizedLink(source, target, dto.getLinkTypeName());
        checkHasTasksLinkExist(linkData);
        Link link = convertToEntity(linkData);
        linkRepository.saveAndFlush(link);
        log.debug("Связь мкжду задачами {}, {} создана", source.getCode(), target.getCode());
        return new LinkResponseDto(link.getId());
    }

    @TransactionRequired
    public List<LinkResponseDto> allTasksLinks(String taskId, String projectId) throws NotFoundException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        TaskModel task = findTaskByIdAndProject(taskId, data.getProject());
        List<Link> links = linkRepository.findLinksByTaskId(task.getId());
        return linkMapper.convertToDto(links, task);
    }
}