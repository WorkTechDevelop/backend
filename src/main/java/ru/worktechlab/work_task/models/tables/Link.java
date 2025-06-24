package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "link")
@NoArgsConstructor
@Getter
@Setter
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @OneToOne
    @JoinColumn(name = "task_master_id", referencedColumnName = "id")
    private TaskModel task_master;

    @OneToOne
    @JoinColumn(name = "task_slave_id", referencedColumnName = "id")
    private TaskModel task_slave;

    @OneToOne
    @JoinColumn(name = "link_type_id", referencedColumnName = "id")
    private LinkType linkType;

    public Link(TaskModel master, TaskModel slave, LinkType linkType) {
        this.task_master = master;
        this.task_slave = slave;
        this.linkType = linkType;
    }
}