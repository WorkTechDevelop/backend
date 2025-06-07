package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.models.tables.ProjectStatus;

import java.util.List;
import java.util.Optional;

@Transactional
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {

    @Query("from ProjectStatus ps where ps.project = :project")
    List<ProjectStatus> findStatusesByProject(Project project);

    @Query("from ProjectStatus ps where ps.project = :project and ps.id = :statusId")
    Optional<ProjectStatus> findStatusesByProjectAndId(Project project, long statusId);
}
