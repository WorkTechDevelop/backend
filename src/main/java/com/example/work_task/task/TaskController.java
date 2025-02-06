package com.example.work_task.task;


import com.example.work_task.model.db.Sprints;
import com.example.work_task.model.db.TaskModel;
import com.example.work_task.model.db.enums.StatusName;
import com.example.work_task.model.db.enums.Priority;
import com.example.work_task.model.db.enums.TaskType;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("work-task/v1/task")
@Slf4j
@AllArgsConstructor
public class TaskController {
    private TaskService taskService;
    private SprintsRepository sprintsRepository;

    private static final String ERROR_TITLE_FORMAT = "Некорректный формат поля TITLE";
    private static final String ERROR_DESCRIPTION_FORMAT = "Некорректный формат поля DESCRIPTION";
    private static final String ERROR_SPRINT_NOT_FOUND_OR_CLOSED = "Спринт закрыт или не существует";
    private static final String ERROR_ESTIMATION_FORMAT = "Некорректный формат поля ESTIMATION";
    private static final String ERROR_PRIORITY_VALUE = "Некорректное значение поля PRIORITY";
    private static final String ERROR_TASK_TYPE_VALUE = "Некорректное значение поля TASK_TYPE";
    private static final String ERROR_STATUS_VALUE = "Некорректное значение поля STATUS";

    @PostMapping("/createTask")
    public ResponseEntity<TaskCreationResponse> createTask(
            @RequestBody TaskModel taskModel,
            @RequestHeader("Authorization") String jwtToken) {

        log.info("Creating task with model: {}", taskModel);
        List<String> validationErrors = validateTask(taskModel);

        if (!validationErrors.isEmpty()) {
            log.error("Validation errors: {}", validationErrors);
            return ResponseEntity.badRequest()
                    .body(new TaskCreationResponse(validationErrors));
        }

        try {
            String taskId = taskService.createTask(taskModel, jwtToken);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new TaskCreationResponse(taskId));
        } catch (RuntimeException e) {
            log.error("Error creating task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TaskCreationResponse(e.getMessage()));
        }
    }

    private List<String> validateTask(TaskModel taskModel) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(taskModel.getTitle())) {
            errors.add(ERROR_TITLE_FORMAT);
        } else if (taskModel.getTitle().length() > 255) {
            errors.add(ERROR_TITLE_FORMAT);
        }

        if (taskModel.getDescription() != null && taskModel.getDescription().length() > 4096) {
            errors.add(ERROR_DESCRIPTION_FORMAT);
        }

        if (taskModel.getSprintId() != null) {
            Sprints sprint = findSprintById(taskModel.getSprintId());
            if (sprint == null || !sprint.isActive()) {
                errors.add(ERROR_SPRINT_NOT_FOUND_OR_CLOSED);
            }
        }

        if (taskModel.getEstimation() == null) {
            errors.add(ERROR_ESTIMATION_FORMAT);
        }

        if (isValueInEnum(Priority.class, taskModel.getPriority().toString())) {
            errors.add(ERROR_PRIORITY_VALUE);
        }

        if (isValueInEnum(TaskType.class, taskModel.getTaskType().toString())) {
            errors.add(ERROR_TASK_TYPE_VALUE);
        }

        if (isValueInEnum(StatusName.class, taskModel.getStatus().toString())) {
            errors.add(ERROR_STATUS_VALUE);
        }

        return errors;
    }

    private Sprints findSprintById(String sprintId) {
        return sprintsRepository.findById(sprintId).orElse(null);
    }

    private <T extends Enum<T>> boolean isValueInEnum(Class<T> enamClass, String value) {
        return Stream.of(enamClass.getEnumConstants())
                .anyMatch(e -> e.name().equalsIgnoreCase(value));
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
