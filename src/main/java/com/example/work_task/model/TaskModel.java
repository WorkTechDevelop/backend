package com.example.work_task.model;

import com.example.work_task.model.enums.Priority;
import com.example.work_task.model.enums.StatusName;
import com.example.work_task.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "task_model")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private Priority priority;

    @Column
    private Integer creator;

    @Column
    private Integer assignee;

    @Column
    private Integer projectId;

    @Column
    private Integer sprintId;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskType taskType;

    @Column
    private Integer estimation;

    @Enumerated(EnumType.STRING)
    @Column
    private StatusName status;

    @Column
    private Date creationDate;

    @Column
    private Date updateDate;
}
