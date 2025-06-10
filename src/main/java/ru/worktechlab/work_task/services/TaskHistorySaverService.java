package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.repositories.TaskHistoryRepository;
import ru.worktechlab.work_task.utils.TaskChangeDetector;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskHistorySaverService {

    private final TaskHistoryRepository repository;
    private final TaskChangeDetector taskChangeDetector;

    public void saveTaskModelChanges(TaskModel oldEntity, TaskModel newEntity) {
        List<TaskHistory> histories = taskChangeDetector.detectChanges(oldEntity, newEntity);
        saveAll(histories);
    }

    public void saveTaskModelStatusChanges(TaskModel oldEntity, TaskModel newEntity) {
        List<TaskHistory> histories = taskChangeDetector.detectStatusChange(oldEntity, newEntity);
        saveAll(histories);
    }

    private void saveAll(List<TaskHistory> history) {
        if (history == null || history.isEmpty()) return;
        repository.saveAll(history);
    }
}
