package ru.worktechlab.work_task.task;

import jakarta.transaction.Transactional;
import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.db.enums.StatusName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private JwtUtils jwtUtils;

    public String createTask(TaskModel taskModel, String jwtToken) {
        taskModel.setId(UUID.randomUUID().toString());
        taskModel.setCreator(jwtUtils.getUserGuidFromJwtToken(formatJwtToken(jwtToken)));
        taskModel.setStatus(StatusName.NEW.toString());
        taskModel.setCreationDate(new Timestamp(System.currentTimeMillis()));
        taskModel.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        TaskModel createdTask = taskRepository.save(taskModel);
        return createdTask.getId();
    }

    public String formatJwtToken(String jwtToken) {
        return jwtToken.replace("Bearer", "").trim();
    }

    @Transactional
    public String updateTask(TaskModel taskModel) {
        TaskModel existingTask = taskRepository.findById(taskModel.getId())
                .orElseThrow(() -> new RuntimeException(String.format("Задача с id: %s не найдена", taskModel.getId())));

        existingTask.setTitle(taskModel.getTitle());
        existingTask.setDescription(taskModel.getDescription());
        existingTask.setPriority(taskModel.getPriority());
        existingTask.setAssignee(taskModel.getAssignee());
        existingTask.setProjectId(taskModel.getProjectId());
        existingTask.setSprintId(taskModel.getSprintId());
        existingTask.setTaskType(taskModel.getTaskType());
        existingTask.setEstimation(taskModel.getEstimation());
        existingTask.setStatus(taskModel.getStatus());
        existingTask.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        taskRepository.save(existingTask);

        return String.format("Задача %s обновлена", existingTask.getId());
    }

    public TaskModel getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Задача по id: %s не найдена", id)));
    }

    public List<TaskModel> getTasksByProjectId(String projectId) {
        return taskRepository.findByProjectId(projectId);
    }
}
