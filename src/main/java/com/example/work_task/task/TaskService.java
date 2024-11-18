package com.example.work_task.task;

import com.example.work_task.model.TaskModel;
import com.example.work_task.servicedb.DbService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final DbService db;

    public List<TaskModel> getAllTasks() {
       return taskRepository.getAllTasks();
    }

    public TaskModel getTaskForId(String id) {
        var result = taskRepository.getTaskById(id);
        if (result == null) throw new IllegalArgumentException("no id"); //examle текст ошибки в логах
       return result;
    }

    public List<TaskModel> getTaskForTitle(String title) {
        var result = db.getTaskByTitle(title);
        if (result == null) throw new IllegalArgumentException("no title"); //examle текст ошибки в логах
        return taskRepository.getTaskByTitle(title);
    }

    public List<TaskModel> getTaskForImplementer(String implementer) {
        var result = db.getTaskByImplementors(implementer);
        if (result == null) throw new IllegalArgumentException("no implementer"); //examle текст ошибки в логах
        return taskRepository.getTaskByImplementors(implementer);
    }

    public List<TaskModel> getTaskForStatus(String status) {
        var result = db.getTaskByStatus(status);
        if (result == null) throw new IllegalArgumentException("no status"); //examle текст ошибки в логах
        return taskRepository.getTaskByStatus(status);
    }

    public void createTask(TaskModel taskModel) {
    }

    public void deleteTask(String id) {
    }

    public void updateTask(String id, TaskModel taskModel) {
    }

    public void updateTitleTask(String id, String title) {
    }

    public void updateImplementerTask(String id, String implementer) {
    }

    public void updateStatusTask(String id, String status) {
    }
}
