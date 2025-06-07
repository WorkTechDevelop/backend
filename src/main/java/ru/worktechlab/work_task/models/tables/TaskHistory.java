package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;
import ru.worktechlab.work_task.utils.UserContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task_history")
public class TaskHistory {
    private final UserContext userContext;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    private Long taskId;
    private String fieldId;
    private String initialValue;
    private String newValue;
    private String userId;
    private LocalDateTime dateTime;

    public TaskHistory of(Long taskId, String fieldId, String oldValue, String newValue) {
        TaskHistory history = new TaskHistory();
        history.taskId = taskId;
        history.fieldId = fieldId;
        history.initialValue = oldValue;
        history.newValue = newValue;
        history.dateTime = LocalDateTime.now();
        history.userId = userContext.getUserData().getUserId();
        return history;
    }
}
