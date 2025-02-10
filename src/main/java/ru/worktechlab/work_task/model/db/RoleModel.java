package ru.worktechlab.work_task.model.db;

import ru.worktechlab.work_task.model.db.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private Roles name;
}
