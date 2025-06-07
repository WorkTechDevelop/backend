package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "project")
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String owner;
    @Column(nullable = false)
    private LocalDate creationDate;
    @Column
    private LocalDate finishDate;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate updateDate;
    @Column
    private String description;
    @Column(name = "is_active")
    private boolean active;
    @Column
    private String creator;
    @Column
    private String finisher;
    @Column(nullable = false)
    private String code;
    @Column
    private Integer taskCounter;
    @OneToMany(mappedBy = "project")
    private List<ProjectStatus> statuses = new ArrayList<>();

    public Project(String name, String owner, String description, boolean active, String creator, String code) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.active = active;
        this.creator = creator;
        this.code = code;
        LocalDate date = LocalDate.now();
        this.creationDate = date;
        this.updateDate = date;
        this.taskCounter = 0;
        if (active)
            this.startDate = date;
    }
}
