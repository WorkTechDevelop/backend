package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    @Column(name = "task_id", nullable = false)
    private String taskId;
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    private String initialValue;

    private String newValue;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private Timestamp dateTime;

    public TaskHistory() {
    }
}
