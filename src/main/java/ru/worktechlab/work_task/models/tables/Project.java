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
    @OneToOne
    private User owner;
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
    @OneToOne
    private User creator;
    @OneToOne
    private User finisher;
    @Column(nullable = false)
    private String code;
    @Column
    private Integer taskCounter;
    @OneToMany(mappedBy = "project")
    private final List<TaskStatus> statuses = new ArrayList<>();
    @OneToMany(mappedBy = "project")
    private final List<Sprint> sprints = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "users_projects",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private final List<User> users = new ArrayList<>();

    public Project(String name, User owner, String description, boolean active, User creator, String code) {
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

    public void finishProject(User user) {
        this.finisher = user;
        LocalDate date = LocalDate.now();
        this.updateDate = date;
        this.finishDate = date;
    }

    public void startProject() {
        this.active = true;
        this.finishDate = null;
        this.finisher = null;
        LocalDate date = LocalDate.now();
        this.updateDate = date;
        if (this.startDate == null)
            this.startDate = date;
    }
}
