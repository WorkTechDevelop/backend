package com.example.work_task.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role_model")
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String isActive;

    @Column
    private String name;
}
