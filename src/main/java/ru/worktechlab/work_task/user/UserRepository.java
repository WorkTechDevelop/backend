package ru.worktechlab.work_task.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.worktechlab.work_task.model.db.Projects;
import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.role_id FROM Users u WHERE u.email = :email")
    Optional<RoleModel> findRoleByEmail(@Param("email") String email);

    @Query("SELECT p FROM Projects p JOIN p.users u WHERE u.id = :id")
    List<Projects> findProjectsByUserId(@Param("id") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.lastProjectId = :lastProjectId WHERE u.id = :id")
    void updateLastProjectIdById(@Param("id") String id, @Param("lastProjectId") String lastProjectId);

    boolean existsByEmail(String email);
}
