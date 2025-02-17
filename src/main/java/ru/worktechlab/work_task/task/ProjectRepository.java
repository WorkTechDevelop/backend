package ru.worktechlab.work_task.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.Projects;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, String> {
}
