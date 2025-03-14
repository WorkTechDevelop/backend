package ru.worktechlab.work_task.task;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.worktechlab.work_task.model.db.TaskModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.responseDTO.UsersTasksInProjectDTO;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, String> {
    List<TaskModel> findByProjectId(String projectId);

    @Query("SELECT NEW ru.worktechlab.work_task.responseDTO.UsersTasksInProjectDTO(CONCAT(u.firstName, ' ', u.lastName), t) " +
            "FROM Users u JOIN TaskModel t ON u.id = t.assignee " +
            "WHERE u.id IN :userIds")
    List<UsersTasksInProjectDTO> findUserTasksByUserIds(@Param("userIds") List<String> userIds);
}


