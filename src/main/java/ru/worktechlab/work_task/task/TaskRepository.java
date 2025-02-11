package ru.worktechlab.work_task.task;

import ru.worktechlab.work_task.model.db.TaskModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

}


