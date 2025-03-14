package ru.worktechlab.work_task.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.UsersProjects;

import java.util.List;

@Repository
public interface UsersProjectsRepository extends JpaRepository<UsersProjects, String> {

    @Query("SELECT up.projectId FROM UsersProjects up WHERE up.userId = :userId")
    List<String> findProjectsByUserId(@Param("userId") String userId);

    @Query("SELECT up.userId FROM UsersProjects up WHERE up.projectId = :projectId")
    List<String> findUserByProjectId(@Param("projectId") String projectId);
}
