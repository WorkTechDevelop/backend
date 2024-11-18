package com.example.work_task.task;


import com.example.work_task.model.TaskModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/all-tasks") //todo продумать над тиом возвращемых зачений
    public List<TaskModel> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskModel getTaskForId(@PathVariable String id) {
        return taskService.getTaskForId(id);
    }

    @GetMapping("/for-title")
    public List<TaskModel> getTaskForTitle(@RequestBody String title) {
        return taskService.getTaskForTitle(title);
    }

    @GetMapping("/for-implementer")
    public  List<TaskModel> getTaskForImplementer(@RequestBody String implementer) {
        return taskService.getTaskForImplementer(implementer);
    }

    @GetMapping("/for-status")
    public  List<TaskModel> getTaskForStatus(@RequestBody String status) {
        return taskService.getTaskForStatus(status);
    }

    @PostMapping("/create")
    public void createTask(@RequestBody TaskModel taskModel) {
        taskService.createTask(taskModel);
    }

    @PostMapping("/delete/{id}")
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/update/{id}")
    public void updateTask(@PathVariable String id, @RequestBody TaskModel taskModel) {
        taskService.updateTask(id, taskModel);
    }

    @PostMapping("/update-title/{id}")
    public void updateTitleTask(@PathVariable String id, @RequestBody String title) {
        taskService.updateTitleTask(id, title);
    }

    @PostMapping("/update-implementer/{id}")
    public void updateImplementerTask(@PathVariable String id, @RequestBody String implementer) {
        taskService.updateImplementerTask(id, implementer);
    }

    @PostMapping("/update-status/{id}")
    public void updateStatusTask(@PathVariable String id, @RequestBody String status) {
        taskService.updateStatusTask(id, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handle(IllegalArgumentException e) {
        return "Текст ошибки который получит пользователь";
    }
}
