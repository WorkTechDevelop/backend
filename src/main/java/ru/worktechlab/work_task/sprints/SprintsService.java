package ru.worktechlab.work_task.sprints;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.responseDTO.SprintInfoDTO;

@Service
@Slf4j
@AllArgsConstructor
public class SprintsService {

    private final SprintsRepository sprintsRepository;

    public SprintInfoDTO getSprintName(Users user) {
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }
}