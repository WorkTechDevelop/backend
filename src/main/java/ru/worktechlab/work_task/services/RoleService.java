package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.exceptions.RoleNotFoundException;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.enums.Roles;
import ru.worktechlab.work_task.repositories.RoleModelRepository;
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleModelRepository roleModelRepository;

    public RoleModel getDefaultRole() {
        return roleModelRepository.findByName(Roles.PROJECT_MEMBER)
                .orElseThrow(() -> new RoleNotFoundException("Роль не найдена в БД"));
    }
}
