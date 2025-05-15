package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users_projects")
public class UsersProjects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "project_id")
    private String projectId;
}
