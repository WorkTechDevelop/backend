package ru.worktechlab.work_task.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.worktechlab.work_task.models.tables.TaskHistory;

import java.sql.Timestamp;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TaskHistoryFactory {

    private final UserContext userContext;

    public TaskHistory createTaskHistory(String taskId, String fieldId, String oldValue, String newValue) {
        TaskHistory history = new TaskHistory();
        history.setTaskId(taskId);
        history.setFieldId(fieldId);
        history.setInitialValue(oldValue);
        history.setNewValue(newValue);
        history.setDateTime(Timestamp.from(Instant.now()));
        history.setUserId(userContext.getUserData().getUserId());

        return history;
    }
}
