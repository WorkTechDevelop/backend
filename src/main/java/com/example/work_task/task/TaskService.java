package com.example.work_task.task;

import com.example.work_task.model.TaskModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<TaskModel> getAllTask() {
        return taskRepository.findAll();
    }

    public Optional<TaskModel> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    public TaskModel createTask(TaskModel taskModel) {
        return taskRepository.save(taskModel);
    }

    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }
}
