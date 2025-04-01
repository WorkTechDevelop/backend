package ru.worktechlab.work_task.sprints;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.Sprints;
import ru.worktechlab.work_task.responseDTO.SprintInfoDTO;

@Repository
public interface SprintsRepository extends JpaRepository<Sprints, String> {

    @Query("SELECT NEW ru.worktechlab.work_task.responseDTO.SprintInfoDTO(s.name, s.startDate, s.endDate) FROM Sprints s WHERE s.projectId = :projectId AND s.active = true")
    SprintInfoDTO getSprintInfoByProjectId(@Param("projectId") String projectId);

    @Query("SELECT s.active FROM Sprints s WHERE s.id = :id")
    Boolean isSprintActive(@Param("id") String id);
}