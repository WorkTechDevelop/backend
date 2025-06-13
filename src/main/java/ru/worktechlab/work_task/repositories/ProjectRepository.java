package ru.worktechlab.work_task.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.models.tables.Project;
import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT NEW ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO(p.name, p.id) FROM Project p WHERE p.id IN :projectIds AND p.active = true")
    List<UsersProjectsDTO> findProjectIdAndNameByIds(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p.active FROM Project p WHERE p.id = :id")
    Boolean isProjectActive(@Param("id") String id);

    @Query("SELECT p.name FROM Project p WHERE p.id = :id")
    String getProjectNameById(@Param("id") String id);

    @Query("SELECT p.code FROM Project p WHERE p.id = :id")
    String getCodeById(@Param("id") String id);

    @Transactional
    @Modifying
    @Query("UPDATE Project p SET p.taskCounter = p.taskCounter + 1 WHERE p.id = :id")
    void incrementCount(@Param("id") String id);

    @Query("SELECT p.taskCounter FROM Project p WHERE p.id = :id")
    Integer getCountById(@Param("id") String id);

    @Query(nativeQuery = true,
        value = "select * from project where id = :projectId for update skip locked")
    Optional<Project> findProjectByForUpdate(String projectId);
}