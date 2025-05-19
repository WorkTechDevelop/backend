package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "sprints")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String name;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column(name = "is_active")
    private boolean active;

    @Column
    private String creator;

    @Column
    private String finisher;

    @Column(name = "project_id")
    private String projectId;

}
