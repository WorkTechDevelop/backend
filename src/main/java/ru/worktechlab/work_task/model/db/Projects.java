package ru.worktechlab.work_task.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "projects")
public class Projects {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private String owner;

    @Column
    private Date creationDate;

    @Column
    private Date updateDate;

    @Column
    private String description;

    @Column(name = "is_active")
    private boolean active;

    @Column
    private String creator;

    @Column
    private String finisher;
}
