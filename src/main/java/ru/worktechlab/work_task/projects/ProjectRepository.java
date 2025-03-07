package ru.worktechlab.work_task.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.Projects;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, String> {
    @Query("SELECT p.id, p.name FROM Projects p WHERE p.id IN :projectIds AND p.active = true")
    List<Object[]> findProjectIdAndNameByIds(@Param("projectIds") List<String> projectIds);
}
