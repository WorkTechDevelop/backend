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

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("SELECT NEW ru.worktechlab.work_task.model.response_dto.UsersProjectsDTO(p.name, p.id) FROM Projects p WHERE p.id IN :projectIds AND p.active = true")
    List<UsersProjectsDTO> findProjectIdAndNameByIds(@Param("projectIds") List<String> projectIds);

    @Query("SELECT p.active FROM Projects p WHERE p.id = :id")
    Boolean isProjectActive(@Param("id") String id);

    @Query("SELECT p.name FROM Projects p WHERE p.id = :id")
    String getProjectNameById(@Param("id") String id);

    @Query("SELECT p.code FROM Projects p WHERE p.id = :id")
    String getCodeById(@Param("id") String id);

    @Transactional
    @Modifying
    @Query("UPDATE Projects p SET p.count = p.count + 1 WHERE p.id = :id")
    void incrementCount(@Param("id") String id);

    @Query("SELECT p.count FROM Projects p WHERE p.id = :id")
    Integer getCountById(@Param("id") String id);


}