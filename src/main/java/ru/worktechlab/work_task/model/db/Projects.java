package ru.worktechlab.work_task.model.db;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

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

    @ManyToMany(mappedBy = "projects")
    private List<Users> users;
}
