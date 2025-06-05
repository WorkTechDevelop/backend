package ru.worktechlab.work_task.models.tables;

import ru.worktechlab.work_task.models.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private Roles name;
}
