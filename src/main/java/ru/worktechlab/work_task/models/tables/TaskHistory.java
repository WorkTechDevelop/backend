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
    private String taskId;
    private String fieldId;
    private String initialValue;
    private String newValue;
    private String userId;
    private Timestamp dateTime;

    public TaskHistory() {
    }
}
