package com.example.work_task.model;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(nullable = false)
    private String implementer;

    @Column(nullable = false)
    private String status;
}
