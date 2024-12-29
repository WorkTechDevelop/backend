package com.example.work_task.model.db;

import com.example.work_task.model.db.enums.Priority;
import com.example.work_task.model.db.enums.StatusName;
import com.example.work_task.model.db.enums.TaskType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "task_model")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 255)
    @Column
    private String title;

    @Size(max = 4096)
    @Column
    private String description;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column
    private Priority priority;

    @Column
    private Integer creator;

    @Column
    private Integer assignee;

    @NonNull
    @Column
    private Integer projectId;

    @Column
    private Integer sprintId;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column
    private TaskType taskType;

    @Column
    private Integer estimation;

    @Enumerated(EnumType.STRING)
    @Column
    private StatusName status;

    @Column
    private Timestamp creationDate;

    @Column
    private Timestamp updateDate;
}
