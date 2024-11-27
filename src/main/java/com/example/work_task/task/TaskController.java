package com.example.work_task.task;


import com.example.work_task.model.TaskModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
   private final TaskService taskService;

    @GetMapping("/{id}")
    public Optional<TaskModel> getTaskById(@PathVariable Integer id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/all")
    public List<TaskModel> getAllTask() {
        return taskService.getAllTask();
    }

    @PostMapping("/create")
    public TaskModel createTask(@RequestBody TaskModel taskModel) {
        return taskService.createTask(taskModel);
    }

    @PostMapping("/delete/{id}")
    public void deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
    }
}
