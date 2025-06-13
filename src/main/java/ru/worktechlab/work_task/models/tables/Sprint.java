package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "sprint")
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
    @Column(name = "is_active")
    private boolean active;
    @OneToOne
    private User creator;
    @OneToOne
    private User finisher;
    @ManyToOne
    private Project project;
}
