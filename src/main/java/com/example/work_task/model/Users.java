package com.example.work_task.model;

import com.example.work_task.model.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "password", nullable = false)
    private String password;
}
