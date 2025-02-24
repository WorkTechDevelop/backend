package ru.worktechlab.work_task.sprints;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.model.db.Sprints;

@Repository
public interface SprintsRepository extends JpaRepository<Sprints, String> {

}
