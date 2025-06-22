package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryDto;
import ru.worktechlab.work_task.utils.TaskChangeDetector;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String comment;

    @Transient
    private TaskChangeDetector taskChangeDetector = new TaskChangeDetector();

    public List<TaskHistoryDto> getChanges() {
        return taskChangeDetector.getTaskHistories();
    }

    public void setComment(String newValue) {
        taskChangeDetector.add(this.comment, newValue);
        this.comment = newValue;
    }

    public Comment(String taskId, User user, String comment) {
        this.taskId = taskId;
        this.user = user;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    public Comment() {
    }
}
