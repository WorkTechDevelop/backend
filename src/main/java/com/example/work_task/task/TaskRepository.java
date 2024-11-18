package com.example.work_task.task;

import com.example.work_task.model.TaskModel;
import com.example.work_task.servicedb.DbService;
import com.example.work_task.servicedb.QuerryCollection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.work_task.tools.awaiter.AwaitUtils.waitUtilDbAction;

@Repository
@AllArgsConstructor
public class TaskRepository {
    private final DbService db;
    private final QuerryCollection qc;


    public List<TaskModel> getAllTasks() {
        return qc.getAllTaskDataRow();
    }

    public TaskModel getTaskById(String id) {
        return db.getTaskDataRow(id, TaskModel::getId);
    }

    public List<TaskModel> getTaskByTitle(String title) {
        return db.getListTaskDataRow(title, TaskModel::getTitle);
    }

    public List<TaskModel> getTaskByImplementors(String implementer) {
        return db.getListTaskDataRow(implementer, TaskModel::getImplementer);
    }

    public List<TaskModel> getTaskByStatus(String status) {
        return db.getListTaskDataRow(status, TaskModel::getStatus);
    }
}

