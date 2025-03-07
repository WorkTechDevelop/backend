package ru.worktechlab.work_task.task;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.db.enums.StatusName;
import ru.worktechlab.work_task.projects.ProjectRepository;
import ru.worktechlab.work_task.projects.UsersProjectsRepository;
import ru.worktechlab.work_task.sprints.SprintsRepository;
import ru.worktechlab.work_task.user.UserRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TaskService {
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private UsersProjectsRepository usersProjectsRepository;
    private ProjectRepository projectRepository;
    private SprintsRepository sprintsRepository;
    private JwtUtils jwtUtils;

    public String createTask(TaskModel taskModel, String jwtToken) {
        taskModel.setId(UUID.randomUUID().toString());
        taskModel.setCreator(jwtUtils.getUserGuidFromJwtToken(jwtToken));
        taskModel.setStatus(StatusName.TODO.toString());
        taskModel.setCreationDate(new Timestamp(System.currentTimeMillis()));
        taskModel.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        TaskModel createdTask = taskRepository.save(taskModel);
        return createdTask.getId();
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

    public List<TaskModel> getProjectTaskByUserGuid(String jwtToken) {
        String userId = jwtUtils.getUserGuidFromJwtToken(jwtToken);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", userId)));
        return taskRepository.findByProjectId(user.getLastProjectId());
    }

    public String getLastProjectId(String jwtToken) {
        String userId = jwtUtils.getUserGuidFromJwtToken(jwtToken);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", userId)));
        return user.getLastProjectId();
    }

    public List<Object[]> getUserProject(String jwtToken) {
        List<String> projectIds = usersProjectsRepository.findProjectsByUserId(jwtUtils.getUserGuidFromJwtToken(jwtToken));
        return projectRepository.findProjectIdAndNameByIds(projectIds);
    }

    public String getSprintName(String jwtToken) {
        Users user = userRepository.findById(jwtUtils.getUserGuidFromJwtToken(jwtToken))
                .orElseThrow(() -> new RuntimeException(String.format("Пользователь не найден по id: %s ", jwtUtils.getUserGuidFromJwtToken(jwtToken))));
        return sprintsRepository.getSprintName(user.getLastProjectId());
    }
}


