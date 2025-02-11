package ru.worktechlab.work_task.task;

import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.db.enums.StatusName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
}
