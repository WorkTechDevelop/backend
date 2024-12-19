package com.example.work_task.task;

import com.example.work_task.model.db.TaskModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

}


