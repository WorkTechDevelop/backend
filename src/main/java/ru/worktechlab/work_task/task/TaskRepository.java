package ru.worktechlab.work_task.task;

import ru.worktechlab.work_task.model.db.TaskModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, String> {
    List<TaskModel> findByProjectId(String projectId);
}


