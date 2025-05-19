package ru.worktechlab.work_task.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.dto.response_dto.SprintInfoDTO;
import ru.worktechlab.work_task.repositories.SprintsRepository;
import ru.worktechlab.work_task.repositories.UserRepository;

@Service
@Slf4j
@AllArgsConstructor
public class SprintsService {
    private final UserRepository userRepository;
    private final SprintsRepository sprintsRepository;
    private final TokenService tokenService;

    public SprintInfoDTO getSprintName(User user) {
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }

    public SprintInfoDTO getSprintName(String jwtToken) {
        User user = userRepository.findById(tokenService.getUserGuidFromJwtToken(jwtToken))
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", tokenService.getUserGuidFromJwtToken(jwtToken))));
        return sprintsRepository.getSprintInfoByProjectId(user.getLastProjectId());
    }
}