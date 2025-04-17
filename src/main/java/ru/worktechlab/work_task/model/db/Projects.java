package ru.worktechlab.work_task.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "projects")
public class Projects {

    @Id
    private String id;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Column
    private String owner;

    @NotNull
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

    @NotNull
    @Column
    private String code;

    @Column
    private Integer count;
}
