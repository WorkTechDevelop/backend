package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.dto.response_dto.SprintInfoDTO;
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

    public SprintInfoDTO getSprintName(User user) {
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }

    public SprintInfoDTO getSprintName() {
        String userId = userContext.getUserData().getUserId();
        User user = userService.findUserById(userId);
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }
}