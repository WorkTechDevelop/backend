package com.example.work_task.task;

import com.example.work_task.model.db.Sprints;
import com.example.work_task.model.db.TaskModel;
import com.example.work_task.model.db.enums.StatusName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private SprintsRepository sprintsRepository;

    public int createTask(TaskModel taskModel) {

        //taskModel.setCreator(creator = GUID текущего пользователя (берётся из токена); //todo task for Segason

        taskModel.setStatus(StatusName.NEW);
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
