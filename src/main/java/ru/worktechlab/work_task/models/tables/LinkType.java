package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.worktechlab.work_task.models.enums.LinkTypeName;

@Entity
@Table(name = "link_type")
@NoArgsConstructor
@Getter
public class LinkType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LinkTypeName name;

    private String description;
}