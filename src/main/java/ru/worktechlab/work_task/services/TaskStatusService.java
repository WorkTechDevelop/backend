package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.statuses.StatusListResponseDto;
import ru.worktechlab.work_task.dto.statuses.TaskStatusDto;
import ru.worktechlab.work_task.dto.statuses.TaskStatusRequestDto;
import ru.worktechlab.work_task.dto.statuses.UpdateRequestStatusesDto;
import ru.worktechlab.work_task.exceptions.BadRequestException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.TaskStatusMapper;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.TaskStatus;
import ru.worktechlab.work_task.repositories.TaskStatusRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;
    private final ProjectsService projectsService;
    private final UserContext userContext;
    private final UserService userService;

    @TransactionRequired
    public StatusListResponseDto getStatuses(String projectId) throws NotFoundException {
        Project project = projectsService.findProjectById(projectId);
        userService.checkHasProjectForUser(userContext.getUserData().getUserId(), projectId);
        StatusListResponseDto response = new StatusListResponseDto(projectId);
        response.setStatuses(project.getStatuses().stream()
                .map(taskStatusMapper::todo)
                .toList());
        return response;
    }

    @TransactionRequired
    public TaskStatusDto createStatus(String projectId,
                                      TaskStatusRequestDto data) throws NotFoundException {
        Project project = projectsService.findProjectById(projectId);
        userService.checkHasProjectForUser(userContext.getUserData().getUserId(), projectId);
        TaskStatus status = taskStatusRepository.saveAndFlush(new TaskStatus(
                data.getPriority(), data.getCode(), data.getDescription(), data.getViewed(), data.getDefaultTaskStatus(), project
        ));
        return taskStatusMapper.todo(status);
    }

    @TransactionRequired
    public StatusListResponseDto updateStatuses(String projectId,
                                              UpdateRequestStatusesDto data) throws NotFoundException, BadRequestException {
        if (CollectionUtils.isEmpty(data.getStatuses()))
            return null;
        Project project = projectsService.findProjectById(projectId);
        userService.checkHasProjectForUser(userContext.getUserData().getUserId(), projectId);
        checkHasDefaultValue(project, data.getStatuses());
        for (TaskStatusRequestDto status : data.getStatuses()) {
            TaskStatus dbStatus = findStatusByIdAndProjectForUpdate(status.getId(), project);
            dbStatus.setPriority(status.getPriority());
            dbStatus.setDescription(status.getDescription());
            dbStatus.setCode(status.getCode());
            dbStatus.setViewed(status.getViewed());
            dbStatus.setDefaultTaskStatus(status.getDefaultTaskStatus());
        }
        taskStatusRepository.flush();
        StatusListResponseDto response = new StatusListResponseDto(projectId);
        response.setStatuses(taskStatusRepository.findStatusesByProject(project).stream()
                .map(taskStatusMapper::todo)
                .toList());
        return response;
    }

    @TransactionMandatory
    public void checkHasDefaultValue(Project project,
                                     List<TaskStatusRequestDto> statuses) throws BadRequestException {
        if (CollectionUtils.isEmpty(statuses))
            return;
        Set<Long> taskStatusIds = statuses.stream()
                .map(TaskStatusRequestDto::getId)
                .collect(Collectors.toSet());
        List<TaskStatus> dbStatuses = taskStatusRepository.findByProjectAndIdsNotIn(project, taskStatusIds);
        long countDefaultStatus = Stream.concat(
                statuses.stream().filter(TaskStatusRequestDto::getDefaultTaskStatus).map(TaskStatusRequestDto::getDefaultTaskStatus),
                dbStatuses.stream().filter(TaskStatus::isDefaultTaskStatus).map(TaskStatus::isDefaultTaskStatus)
        ).count();
        if (countDefaultStatus == 0)
            throw new BadRequestException(String.format("У проекта %s не назначен статус по умолчанию", project.getName()));
        if (countDefaultStatus > 1)
            throw new BadRequestException(String.format("У проекта %s назначено несколько статусов по умолчанию", project.getName()));
    }

    @TransactionMandatory
    public TaskStatus findStatusById(long statusId) throws NotFoundException {
        return taskStatusRepository.findById(statusId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден статус с ИД - %s", statusId))
        );
    }

    @TransactionMandatory
    public TaskStatus findStatusByIdAndProjectForUpdate(long statusId,
                                                        Project project) throws NotFoundException {
        return taskStatusRepository.findStatusesByProjectAndIdForUpdate(project.getId(), statusId).orElseThrow(
                () -> new NotFoundException(String.format("Не найдена задача для проекта %s с ИД - %s", project.getName(), statusId))
        );
    }
}
