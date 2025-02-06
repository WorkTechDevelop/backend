package com.example.work_task.task;


import com.example.work_task.model.db.Sprints;
import com.example.work_task.model.db.TaskModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;
    private SprintsRepository sprintsRepository;

    @PostMapping("/createTask")
    public ResponseEntity<Integer> createTask(@RequestBody TaskModel taskModel
//                                              @RequestHeader("Authorization") String token
    ) {
        List<String> validationErrors = validateTask(taskModel);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", validationErrors));
        }
        int taskId = taskService.createTask(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskId);
    }

    private List<String> validateTask(TaskModel taskModel) {
        List<String> errors = new ArrayList<>();

        if (taskModel.getTitle() == null || taskModel.getTitle().isEmpty() || taskModel.getTitle().length() > 255) {
            errors.add("Некорректный формат поля TITLE");
        }
        if (taskModel.getDescription() != null && taskModel.getDescription().length() > 4096) {
            errors.add("Некорректный формат поля DESCRIPTION");
        }
        if (taskModel.getSprintId() != null) {
            Sprints sprint = findSprintById(taskModel.getSprintId());
            if (sprint == null || !sprint.isActive()) {
                errors.add("Спринт закрыт или не существует");
            }
        }
        if (taskModel.getEstimation() != null) {
            errors.add("Некорректный формат поля ESTIMATION");
        }
        return errors;
    }

    private Sprints findSprintById(Integer sprintId) {
        return sprintsRepository.findById(sprintId).orElse(null);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
                                                                    /*Deprecated*/

//    @GetMapping("/{id}")
//    public Optional<TaskModel> getTaskById(@PathVariable Integer id) {
//        return taskService.getTaskById(id);
//    }
//
//    @GetMapping("/all")
//    public List<TaskModel> getAllTask() {
//        return taskService.getAllTask();
//    }
//
//    @PostMapping("/create")
//    public TaskModel createTask(@RequestBody TaskModel taskModel) {
//        return taskService.createTask(taskModel);
//    }
//
//    @PostMapping("/delete/{id}")
//    public void deleteTask(@PathVariable Integer id) {
//        taskService.deleteTask(id);
//    }
}
