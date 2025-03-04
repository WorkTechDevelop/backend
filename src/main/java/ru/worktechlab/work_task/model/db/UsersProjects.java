package ru.worktechlab.work_task.model.db;

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
