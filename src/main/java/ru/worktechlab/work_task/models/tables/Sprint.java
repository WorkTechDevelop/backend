package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sprint")
@Getter
@Setter
@NoArgsConstructor
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String name;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    private LocalDate createdAt;
    @Column(name = "is_active")
    private boolean active;
    @OneToOne
    private User creator;
    @OneToOne
    private User finisher;
    @ManyToOne
    private Project project;

    public Sprint(String name, LocalDate startDate, LocalDate endDate, User creator, Project project) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creator = creator;
        this.project = project;
        this.active = false;
        this.createdAt = LocalDate.now();
    }
}
