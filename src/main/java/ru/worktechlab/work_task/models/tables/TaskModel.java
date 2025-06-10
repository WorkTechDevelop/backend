package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
@Entity
@Table(name = "task_model")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Size(max = 255)
    @Column
    private String title;

    @Size(max = 4096)
    @Column
    private String description;

    @NotBlank
    @Column
    private String priority;

    @Column
    private String creator;

    @Column
    private String assignee;

    @NonNull
    @Column
    private String projectId;

    @Column
    private String sprintId;

    @NotBlank
    @Column(name = "task_type")
    private String taskType;

    @Column
    private Integer estimation;

    @Column
    private String status;

    @Column
    private Timestamp creationDate;

    @Column
    private Timestamp updateDate;

    @Column
    private String code;

    public TaskModel() {
    }

    public TaskModel(String title, String description, String priority, String assignee,
                     String projectId, String sprintId, String taskType,
                     Integer estimation, String status, String code) {

        this.title = title;
        this.priority = priority;
        this.assignee = assignee;
        this.description = description;
        this.projectId = projectId;
        this.sprintId = sprintId;
        this.taskType = taskType;
        this.estimation = estimation;
        this.status = status;
        this.code = code;
    }

    public static TaskModel copyOf(TaskModel task) {
        return new TaskModel(task.getTitle(), task.getDescription(), task.getPriority(),
                task.getAssignee(), task.getProjectId(), task.getSprintId(),
                task.getTaskType(), task.getEstimation(), task.getStatus(),
                task.getCode());
    }
}
