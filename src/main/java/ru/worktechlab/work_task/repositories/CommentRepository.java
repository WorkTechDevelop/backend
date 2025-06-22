package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.worktechlab.work_task.models.tables.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    Optional<Comment> findById(String id);

    @Query(nativeQuery = true,
            value = "select * from comment where id = :id for update skip locked")
    Optional<Comment> findCommentByIdForUpdate(String id);
}
