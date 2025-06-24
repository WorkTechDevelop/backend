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
import ru.worktechlab.work_task.dto.task_comment.*;
import ru.worktechlab.work_task.dto.task_link.LinkDto;
import ru.worktechlab.work_task.dto.task_link.LinkResponseDto;
import ru.worktechlab.work_task.dto.tasks.TaskDataDto;
import ru.worktechlab.work_task.dto.tasks.TaskModelDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.tasks.UpdateTaskModelDTO;
import ru.worktechlab.work_task.exceptions.DuplicateLinkException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.exceptions.PermissionDeniedException;
import ru.worktechlab.work_task.mappers.CommentMapper;
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
    private final LinkTypeRepository linkTypeRepository;
    private final LinkRepository linkRepository;

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
                data.linkType()
        );
    }

    @TransactionRequired
    public Comment findCommentForUpdateByIdOrElseThrow(String id) {
        return commentRepository.findCommentByIdForUpdate(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден комментарий с таким id - %s", id)
                ));
    }

    @TransactionRequired
    public Comment findCommentByIdOrElseThrow(String id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден комментарий с таким id - %s", id)
                ));
    }

    public LinkType findLinkTypeByIdOrElseThrow(Long id) {
        return linkTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден LinkType с таким id - %s", id)

                ));
    }

    public LinkType findLinkTypeByNameOrElseThrow(LinkTypeName name) {
        return linkTypeRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Не найден LinkType с таким названием - %s", name)

                ));
    }

    public boolean isSameProjects(TaskModel first, TaskModel second) {
        return first.getProject().getId().equals(second.getProject().getId());
    }

    private NormalizedLinkData normalizedLink(TaskModel first, TaskModel second, LinkType linkType) {
        LinkTypeName typeName = linkType.getName();
        if (typeName.inverseExist()) {
            LinkType canonicalLinkType = findLinkTypeByNameOrElseThrow(typeName.getInverse());
            return new NormalizedLinkData(second, first, canonicalLinkType);
        }
        return new NormalizedLinkData(first, second, linkType);
    }

    private void checkHasTasksLinkExist(NormalizedLinkData data) {
        linkRepository.findByTasksLinkedAndType(data.master().getId(), data.slave().getId(), data.linkType().getName())
                .ifPresent(existing -> {
                    throw new DuplicateLinkException("Связь между задачами уже существует");
                });
    }

    @TransactionRequired
    public CommentResponseDto createComment(CommentDto dto) throws NotFoundException {
        log.debug("Создать комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        TaskModel task = findTaskByIdOrThrow(dto.getTaskId());
        Comment comment = convertToEntity(dto, data.getUser(), task);
        commentRepository.saveAndFlush(comment);
        return commentMapper.toDto(comment);
    }

    @TransactionRequired
    public CommentResponseDto updateComment(UpdateCommentDto dto) throws NotFoundException {
        log.debug("Обновить комментарий к задаче");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        Comment comment = findCommentForUpdateByIdOrElseThrow(dto.getCommentId());
        TaskModel task = findTaskByIdOrThrow(dto.getTaskId());
        taskHistorySaverService.saveTaskCommentChanges(comment, dto, data.getUser(),task);
        commentRepository.flush();
        log.debug("Комментарий обновлен: id={}", comment.getId());
        return commentMapper.toDto(comment);
    }

    @TransactionRequired
    public ApiResponse deleteComment(String commentId, String projectId) throws NotFoundException {
        log.debug("Удалить комментарий к задаче");
        findCommentByIdOrElseThrow(commentId);
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        commentRepository.deleteCommentById(commentId);
        commentRepository.flush();
        ApiResponse apiResponse = new ApiResponse("Комментарий успешно удалён");
        log.info("Пользователь {} удалил комментарий {}", data.getUser().getFirstName() + data.getUser().getLastName(), commentId);
        return apiResponse;
    }

    @TransactionRequired
    public List<AllTasksCommentsResponseDto> allTasksComments(AllTasksCommentsDto dto) throws NotFoundException {
        log.debug("Получить все комментарии к задаче");
        findTaskByIdOrThrow(dto.getTaskId());
        checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        List<Comment> comments = commentRepository.findAllByTaskIdOrderByCreatedAtAsc(dto.getTaskId());
        return commentMapper.toGetAllDtoList(comments);
    }

    @TransactionRequired
    public LinkResponseDto linkTask(LinkDto dto) throws NotFoundException {
        checkerUtil.findAndCheckProjectUserData(dto.getProjectId(), false, false);
        TaskModel taskFirst = findTaskByIdOrThrow(dto.getTaskIdFirst());
        TaskModel taskSecond = findTaskByIdOrThrow(dto.getTaskIdSecond());
        if (!isSameProjects(taskFirst, taskSecond)) {
            throw new PermissionDeniedException("Нельзя связать задачи из разных пректов");
        }
        LinkType initialLinkType = findLinkTypeByIdOrElseThrow(dto.getLinkTypeId());
        NormalizedLinkData linkData = normalizedLink(taskFirst, taskSecond, initialLinkType);
        checkHasTasksLinkExist(linkData);
        Link link = convertToEntity(linkData);
        linkRepository.saveAndFlush(link);
        return new LinkResponseDto(link.getId());
    }
}