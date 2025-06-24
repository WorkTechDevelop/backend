package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.worktechlab.work_task.models.enums.LinkTypeName;
import ru.worktechlab.work_task.models.tables.Link;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, String> {

    @Query("""
    SELECT l FROM Link l
    WHERE 
        (
            (l.task_master.id = :firstId AND l.task_slave.id = :secondId)
         OR
            (l.task_master.id = :secondId AND l.task_slave.id = :firstId)
        )
      AND l.linkType.name = :linkTypeName
""")
    Optional<Link> findByTasksLinkedAndType(
            @Param("firstId") String firstTaskId,
            @Param("secondId") String secondTaskId,
            @Param("linkTypeName") LinkTypeName linkTypeName
    );
}