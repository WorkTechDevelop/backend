package ru.worktechlab.work_task.sprints;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.jwt.TokenService;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.responseDTO.SprintInfoDTO;
import ru.worktechlab.work_task.user.UserRepository;

@Service
@Slf4j
@AllArgsConstructor
public class SprintsService {
    private final UserRepository userRepository;
    private final SprintsRepository sprintsRepository;
    private final TokenService tokenService;

    public SprintInfoDTO getSprintName(Users user) {
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }

    public SprintInfoDTO getSprintName(String jwtToken) {
        Users user = userRepository.findById(tokenService.getUserGuidFromJwtToken(jwtToken))
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", tokenService.getUserGuidFromJwtToken(jwtToken))));
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }
}