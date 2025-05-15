package ru.worktechlab.work_task.repositories;

import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleModelRepository extends JpaRepository<RoleModel, Integer> {
    Optional<RoleModel> findByName(Roles name);
}
