package ru.worktechlab.work_task.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.Projects;
import ru.worktechlab.work_task.responseDTO.UsersProjectsDTO;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, String> {
    @Query("SELECT NEW ru.worktechlab.work_task.responseDTO.UsersProjectsDTO(p.name, p.id) FROM Projects p WHERE p.id IN :projectIds AND p.active = true")
    List<UsersProjectsDTO> findProjectIdAndNameByIds(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p.active FROM Projects p WHERE p.id = :id")
    Boolean isProjectActive(@Param("id") String id);
}