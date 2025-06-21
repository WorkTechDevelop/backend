package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.UserAndProjectData;
import ru.worktechlab.work_task.dto.sprints.ActivateSprintDtoRequest;
import ru.worktechlab.work_task.dto.sprints.SprintDtoRequest;
import ru.worktechlab.work_task.dto.sprints.SprintInfoDTO;
import ru.worktechlab.work_task.exceptions.BadRequestException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.SprintMapper;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.Sprint;
import ru.worktechlab.work_task.repositories.SprintsRepository;
import ru.worktechlab.work_task.utils.CheckerUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class SprintsService {
    private final SprintsRepository sprintsRepository;
    private final SprintMapper sprintMapper;
    private final CheckerUtil checkerUtil;

    @TransactionRequired
    public SprintInfoDTO getActiveSprint(String projectId) throws NotFoundException {
        log.debug("Вывод информации о спринте");
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        Project project = data.getProject();
        Sprint sprint = sprintsRepository.getSprintInfoByProjectId(project);
        if (sprint == null)
            throw new NotFoundException(String.format(
                    "Не найден активный спринт для проекта %s", project.getName()
            ));
        return sprintMapper.toSprintInfoDto(sprint);
    }

    @TransactionRequired
    public SprintInfoDTO createSprint(String projectId,
                                      SprintDtoRequest request) throws NotFoundException {
        UserAndProjectData data = checkerUtil.findAndCheckProjectUserData(projectId, false, false);
        Sprint sprint = sprintsRepository.saveAndFlush(new Sprint(
                request.getName(), request.getStartDate(), request.getEndDate(), data.getUser(), data.getProject()
        ));
        Sprint dbSprint = findSprintById(sprint.getId());
        return sprintMapper.toSprintInfoDto(dbSprint);
    }

    @TransactionMandatory
    public Sprint findSprintById(String sprintId) throws NotFoundException {
        return sprintsRepository.findById(sprintId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден спринт с ИД - %s", sprintId))
        );
    }

    @TransactionRequired
    public SprintInfoDTO activateSprint(String sprintId,
                                        ActivateSprintDtoRequest data) throws NotFoundException, BadRequestException {
        Sprint sprint = findSprintByIdForUpdate(sprintId);
        checkerUtil.checkHasProjectForUser(sprint.getProject());
        checkHasActiveSprint(sprint, data.isActivate());
        sprint.setActive(data.isActivate());
        sprintsRepository.flush();
        Sprint dbSprint = findSprintById(sprint.getId());
        return sprintMapper.toSprintInfoDto(dbSprint);
    }

    @TransactionMandatory
    public void checkHasActiveSprint(Sprint sprint,
                                     boolean active) throws BadRequestException {
        if (!active || sprint.isActive())
            return;
        boolean hasActiveSprint = sprintsRepository.hasActiveSprint(sprint.getProject().getId());
        if (hasActiveSprint)
            throw new BadRequestException(String.format("У проекта %s есть незавершенный спринт", sprint.getProject().getName()));
    }

    @TransactionMandatory
    public Sprint findSprintByIdForUpdate(String sprintId) throws NotFoundException {
        return sprintsRepository.findSprintByIdForUpdate(sprintId).orElseThrow(
                () -> new NotFoundException(String.format("Не найден спринт с ИД - %s", sprintId))
        );
    }

    @TransactionMandatory
    public Sprint findSprintByIdAndProject(String sprintId,
                                           Project project) throws NotFoundException {
        return sprintsRepository.findSprintByIdAndProject(sprintId, project).orElseThrow(
                () -> new NotFoundException(String.format("Для проекта %s не найден спринт с ИД - %s", project.getName(), sprintId))
        );
    }
}