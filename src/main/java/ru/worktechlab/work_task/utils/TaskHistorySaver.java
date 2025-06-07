package ru.worktechlab.work_task.utils;

import org.springframework.beans.factory.annotation.Autowired;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.repositories.TaskHistoryRepository;

import java.util.List;

public class TaskHistorySaver {

    @Autowired
    public void setRepository(TaskHistoryRepository repository) {
        TaskHistorySaver.repository = repository;
    }

    private static TaskHistoryRepository repository;

    public static void saveAll(List<TaskHistory> history) {
        if (history == null || history.isEmpty()) return;
        repository.saveAll(history);
    }
}
