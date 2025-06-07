package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.statuses.ProjectStatusDto;
import ru.worktechlab.work_task.dto.statuses.ProjectStatusRequestDto;
import ru.worktechlab.work_task.dto.statuses.StatusListResponseDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.ProjectStatusMapper;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.ProjectStatus;
import ru.worktechlab.work_task.repositories.ProjectStatusRepository;
import ru.worktechlab.work_task.utils.UserContext;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectStatusService {

    private final ProjectStatusRepository projectStatusRepository;
    private final ProjectStatusMapper projectStatusMapper;
    private final ProjectsService projectsService;
    private final UserContext userContext;
    private final UserService userService;

    @TransactionRequired
    public StatusListResponseDto getStatuses(String projectId) throws NotFoundException {
        Project project = projectsService.findProjectById(projectId);
        userService.checkHasProjectForUser(userContext.getUserData().getUserId(), projectId);
        StatusListResponseDto response = new StatusListResponseDto(projectId);
        response.setStatuses(project.getStatuses().stream()
                .map(projectStatusMapper::todo)
                .toList());
        return response;
    }

    @TransactionRequired
    public ProjectStatusDto createStatus(String projectId,
                                         ProjectStatusRequestDto data) throws NotFoundException {
        Project project = projectsService.findProjectById(projectId);
        userService.checkHasProjectForUser(userContext.getUserData().getUserId(), projectId);
        ProjectStatus status = projectStatusRepository.saveAndFlush(new ProjectStatus(
                data.getPriority(), data.getCode(), data.getDescription(), data.getViewed(), project
        ));
        return projectStatusMapper.todo(status);
    }

    @TransactionRequired
    public ProjectStatusDto updateStatus(String projectId,
                                         long statusId,
                                         ProjectStatusRequestDto data) throws NotFoundException {
        Project project = projectsService.findProjectById(projectId);
        userService.checkHasProjectForUser(userContext.getUserData().getUserId(), projectId);
        ProjectStatus status = findStatusByIdAndProject(statusId, project);
        status.setPriority(data.getPriority());
        status.setDescription(data.getDescription());
        status.setCode(data.getCode());
        status.setViewed(data.getViewed());
        projectStatusRepository.flush();
        return projectStatusMapper.todo(findStatusById(statusId));
    }

    @TransactionMandatory
    public ProjectStatus findStatusById(long statusId) throws NotFoundException {
        return projectStatusRepository.findById(statusId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден статус с ИД - %s", statusId))
        );
    }

    @TransactionMandatory
    public ProjectStatus findStatusByIdAndProject(long statusId,
                                                  Project project) throws NotFoundException {
        return projectStatusRepository.findStatusesByProjectAndId(project, statusId).orElseThrow(
                () -> new NotFoundException(String.format("Не найдена задача для проекта %s с ИД - %s", project.getName(), statusId))
        );
    }
}
