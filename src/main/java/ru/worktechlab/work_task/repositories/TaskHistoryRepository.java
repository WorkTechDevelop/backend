package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.worktechlab.work_task.models.tables.TaskHistory;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, String> {
}
