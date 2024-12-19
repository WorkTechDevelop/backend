package com.example.work_task.model.db;

import com.example.work_task.model.db.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "roles")
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private Roles name;

    @OneToMany(mappedBy = "role")
    private List<Users> users;
}
