package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Data;
import ru.worktechlab.work_task.models.enums.Gender;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role_id")
    private String role_id;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_project_id")
    private String lastProjectId;
}
