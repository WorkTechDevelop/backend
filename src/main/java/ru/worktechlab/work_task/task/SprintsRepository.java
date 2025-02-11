package ru.worktechlab.work_task.task;

import ru.worktechlab.work_task.model.db.Sprints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintsRepository extends JpaRepository<Sprints, String> {

}
