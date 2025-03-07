package ru.worktechlab.work_task.sprints;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.Sprints;

@Repository
public interface SprintsRepository extends JpaRepository<Sprints, String> {

    @Query("SELECT s.name FROM Sprints s WHERE s.projectId = :projectId AND s.active = true")
    String getSprintName(@Param("projectId") String projectId);
}
