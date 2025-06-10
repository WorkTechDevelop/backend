package ru.worktechlab.work_task.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.worktechlab.work_task.interfaces.EntityChangeDetector;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.models.tables.TaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
@AllArgsConstructor
public class TaskChangeDetector implements EntityChangeDetector<TaskModel> {

    private TaskHistoryFactory historyFactory;

    private static final Map<String, BiFunction<TaskModel, TaskModel, Boolean>> compareFields = Map.of(
            "TITLE", (oldF, newF) -> !Objects.equals(oldF.getTitle(), newF.getTitle()),
            "PRIORITY", (oldF, newF) -> !Objects.equals(oldF.getPriority(), newF.getPriority()),
            "ASSIGNEE", (oldF, newF) -> !Objects.equals(
                    oldF.getAssignee() != null ? oldF.getAssignee() : null,
                    newF.getAssignee() != null ? newF.getAssignee() : null
            ),
            "DESCRIPTION", (oldF, newF) -> !Objects.equals(
                    oldF.getDescription() != null ? oldF.getDescription() : null,
                    newF.getDescription() != null ? newF.getDescription() : null
            ),
            "SPRINT_ID", (oldF, newF) -> !Objects.equals(
                    oldF.getSprintId() != null ? oldF.getSprintId() : null,
                    newF.getSprintId() != null ? newF.getSprintId() : null

            ),
            "TASK_TYPE", (oldF, newF) -> !Objects.equals(
                    oldF.getTaskType(), newF.getTaskType()
            ),
            "ESTIMATION", (oldF, newF) -> !Objects.equals(
                    oldF.getEstimation() != null ? oldF.getEstimation() : null,
                    newF.getEstimation() != null ? newF.getEstimation() : null
            ),
            "CODE", (oldF, newF) -> !Objects.equals(oldF.getCode(), newF.getCode())
    );

    private static final Map<String, BiFunction<TaskModel, TaskModel, Boolean>> compareStatus = Map.of(
            "STATUS", (oldF, newF) -> !Objects.equals(oldF.getStatus(), newF.getStatus())
    );

    @Override
    public List<TaskHistory> detectChanges(TaskModel oldEntity, TaskModel newEntity) {
        List<TaskHistory> changes = new ArrayList<>();

        compareFields.forEach((field, changedCheck) -> {
            if (changedCheck.apply(oldEntity, newEntity)) {
                String oldVal = getFieldValueAsString(oldEntity, field);
                String newVal = getFieldValueAsString(newEntity, field);
                changes.add(historyFactory.createTaskHistory(newEntity.getId(), field, oldVal, newVal));
            }
        });
        return changes;
    }

    public List<TaskHistory> detectStatusChange(TaskModel oldEntity, TaskModel newEntity) {
        List<TaskHistory> changes = new ArrayList<>();

        compareStatus.forEach((field, changedCheck) -> {
            if (changedCheck.apply(oldEntity, newEntity)) {
                String oldVal = getFieldValueAsString(oldEntity, field);
                String newVal = getFieldValueAsString(newEntity, field);
                changes.add(historyFactory.createTaskHistory(newEntity.getId(), field, oldVal, newVal));
            }
        });
        return changes;
    }

    private String getFieldValueAsString(TaskModel task, String field) {
        switch (field) {
            case "TITLE":
                return task.getTitle();
            case "PRIORITY":
                return task.getPriority();
            case "ASSIGNEE":
                return task.getAssignee() != null ? task.getAssignee() : null;
            case "DESCRIPTION":
                return task.getDescription() != null ? task.getDescription() : null;
            case "SPRINT_ID":
                return task.getSprintId() != null ? task.getSprintId() : null;
            case "TASK_TYPE":
                return task.getTaskType();
            case "ESTIMATION":
                return task.getEstimation() != null ? task.getEstimation().toString() : null;
            case "CODE":
                return task.getCode();
            case "STATUS":
                return task.getStatus();
            default:
                return null;
        }
    }
}
