package ru.worktechlab.work_task.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.RoleNotFoundException;
import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.enums.Roles;
import ru.worktechlab.work_task.user.RoleModelRepository;
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleModelRepository roleModelRepository;

    public RoleModel getDefaultRole() {
        return roleModelRepository.findByName(Roles.PROJECT_MEMBER)
                .orElseThrow(() -> new RoleNotFoundException("Роль не найдена в БД"));
    }
}
