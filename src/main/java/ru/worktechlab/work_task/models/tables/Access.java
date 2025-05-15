package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "access")
@IdClass(AccessId.class)
public class Access {

    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "id", nullable = false, length = 36, unique = true)
    private String id;

    @Column(name = "project_id", nullable = false)
    private Integer projectId;
}
