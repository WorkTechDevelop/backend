package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.Sprint;

@Repository
public interface SprintsRepository extends JpaRepository<Sprint, String> {

    @Query("FROM Sprint WHERE project = :project AND active = true")
    Sprint getSprintInfoByProjectId(Project project);

    @Query("SELECT s.active FROM Sprint s WHERE s.id = :id")
    Boolean isSprintActive(@Param("id") String id);
}