package com.example.work_task.user;

import com.example.work_task.model.db.RoleModel;
import com.example.work_task.model.db.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleModelRepository extends JpaRepository<RoleModel, Integer> {
    Optional<RoleModel> findByName(Roles name);
}
