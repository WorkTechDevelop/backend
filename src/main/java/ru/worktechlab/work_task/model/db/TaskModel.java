package ru.worktechlab.work_task.model.db;

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

    public TaskModel() {
    }
}
