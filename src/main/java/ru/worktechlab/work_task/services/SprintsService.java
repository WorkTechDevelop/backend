package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.dto.sprints.SprintInfoDTO;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.SprintMapper;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.Sprint;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.SprintsRepository;
import ru.worktechlab.work_task.utils.UserContext;

@Service
@Slf4j
@RequiredArgsConstructor
public class SprintsService {
    private final SprintsRepository sprintsRepository;
    private final UserService userService;
    private final UserContext userContext;
    private final ProjectsService projectsService;
    private final SprintMapper sprintMapper;

    @TransactionRequired
    public SprintInfoDTO getActiveSprint() throws NotFoundException {
        log.debug("Вывод информации о спринте");
        String userId = userContext.getUserData().getUserId();
        User user = userService.findActiveUserById(userId);
        if (user.getLastProjectId() == null)
            throw new NotFoundException("Не найден активный проект");
        Project project = projectsService.findProjectById(user.getLastProjectId());
        Sprint sprint = sprintsRepository.getSprintInfoByProjectId(project);
        if (sprint == null)
            throw new NotFoundException(String.format(
                    "Не найден активный спринт для проекта %s", project.getName()
            ));
        return sprintMapper.toSprintInfoDto(sprint);
    }
}