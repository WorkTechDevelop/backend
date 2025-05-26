package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, String> {
    List<TaskModel> findByProjectId(String projectId);
    Optional<TaskModel> findByCode(String code);

    @Query("SELECT NEW ru.worktechlab.work_task.dto.response_dto.UsersTasksInProjectDTO(CONCAT(u.firstName, ' ', u.lastName), t) " +
            "FROM User u JOIN TaskModel t ON u.id = t.assignee " +
            "WHERE u.id IN :userIds")
    List<UsersTasksInProjectDTO> findUserTasksByUserIds(@Param("userIds") List<String> userIds);
}


