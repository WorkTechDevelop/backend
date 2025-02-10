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
    private SprintsRepository sprintsRepository;
    private JwtUtils jwtUtils;

    public String createTask(TaskModel taskModel, String jwtToken) {
        taskModel.setId(UUID.randomUUID().toString());
        taskModel.setCreator(jwtUtils.getUserGuidFromJwtToken(jwtToken));
        taskModel.setStatus(StatusName.NEW.toString());
        taskModel.setCreationDate(new Timestamp(System.currentTimeMillis()));
        taskModel.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        TaskModel createdTask = taskRepository.save(taskModel);
        return createdTask.getId();
    }


                                                /*Deprecated*/


//        public List<TaskModel> getAllTask() {
//        return taskRepository.findAll();
//    }
//
//    public Optional<TaskModel> getTaskById(Integer id) {
//        return taskRepository.findById(id);
//    }

//    public void deleteTask(Integer id) {
//        taskRepository.deleteById(id);
//    }
}
