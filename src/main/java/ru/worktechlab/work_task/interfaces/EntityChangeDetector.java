package ru.worktechlab.work_task.interfaces;

import ru.worktechlab.work_task.models.tables.TaskHistory;

import java.util.List;

public interface EntityChangeDetector <T>{
    List<TaskHistory> detectChanges(T oldEntity, T newEntity);
}
