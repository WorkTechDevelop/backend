package com.example.work_task.task;

import com.example.work_task.model.TaskModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.work_task.tools.awaiter.AwaitUtils.waitUtilDbAction;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Integer> {

}


