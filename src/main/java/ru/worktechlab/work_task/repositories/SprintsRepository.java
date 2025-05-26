package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.models.tables.Sprint;
import ru.worktechlab.work_task.dto.response_dto.SprintInfoDTO;

@Repository
public interface SprintsRepository extends JpaRepository<Sprint, String> {

    @Query("SELECT NEW ru.worktechlab.work_task.dto.response_dto.SprintInfoDTO(s.name, s.startDate, s.endDate) FROM Sprint s WHERE s.projectId = :projectId AND s.active = true")
    SprintInfoDTO getSprintInfoByProjectId(@Param("projectId") String projectId);

    @Query("SELECT s.active FROM Sprint s WHERE s.id = :id")
    Boolean isSprintActive(@Param("id") String id);
}