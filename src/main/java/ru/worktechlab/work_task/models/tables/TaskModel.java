package ru.worktechlab.work_task.models.tables;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryDto;
import ru.worktechlab.work_task.utils.TaskChangeDetector;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Data
@Entity
@Table(name = "task_model")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private TaskChangeDetector taskChangeDetector = new TaskChangeDetector();

    public List<TaskHistoryDto> getChanges() {
        return taskChangeDetector.getTaskHistories();
    }

    public void setCodeHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Код задачи", oldValue, newValue);
    }

    public void setTitleHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Заголовок", oldValue, newValue);
    }

    public void setPriorityHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Приоритет", oldValue, newValue);
    }

    public void setAssigneeHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Исполнитель", oldValue, newValue);
    }

    public void setDescriptionHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Описание", oldValue, newValue);
    }

    public void setSprintIdHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Идентификатор спринта", oldValue, newValue);
    }

    public void setTaskTypeHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Тип задачи", oldValue, newValue);
    }

    public void setEstimationHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Оценка задачи", oldValue, newValue);
    }

    public void setStatusHistory(String oldValue, String newValue) {
        taskChangeDetector.add("Статус задачи", oldValue, newValue);
    }

    public TaskModel() {
    }

    public TaskModel(String title, String description, String priority, String assignee,
                     String projectId, String sprintId, String taskType,
                     Integer estimation, String status, String code) {

        this.title = title;
        this.priority = priority;
        this.assignee = assignee;
        this.description = description;
        this.projectId = projectId;
        this.sprintId = sprintId;
        this.taskType = taskType;
        this.estimation = estimation;
        this.status = status;
        this.code = code;
    }
}
