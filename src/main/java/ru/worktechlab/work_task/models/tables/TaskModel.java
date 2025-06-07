package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.worktechlab.work_task.interfaces.TrackableEntity;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
@Entity
@Table(name = "task_model")
public class TaskModel implements TrackableEntity<TaskModel> {

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

    @Column
    private String code;

    @Transient
    private TaskModel previousState;

    @PostLoad
    public void captureState() {
        this.previousState = this.clone();
    }

    @Override
    public TaskModel clone() {
        TaskModel copy = new TaskModel();
        copy.setId(this.id);
        copy.setTitle(this.title);
        copy.setDescription(this.description);
        copy.setPriority(this.priority);
        copy.setAssignee(this.assignee);
        copy.setSprintId(this.sprintId);
        copy.setTaskType(this.taskType);
        copy.setEstimation(this.estimation);
        copy.setStatus(this.status);
        // ... только нужные поля для сравнения
        return copy;
    }



    @Override
    public void setPreviousState(TaskModel previous) {
        this.previousState = previous;
    }

    @Override
    public TaskModel getPreviousState() {
        return this.previousState;
    }

    public TaskModel() {
    }
}
