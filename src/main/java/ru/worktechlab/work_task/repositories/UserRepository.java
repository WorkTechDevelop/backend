package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.role_id FROM Users u WHERE u.email = :email")
    Optional<RoleModel> findRoleByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.lastProjectId = :lastProjectId WHERE u.id = :id")
    void updateLastProjectIdById(@Param("id") String id, @Param("lastProjectId") String lastProjectId);

    boolean existsByEmail(String email);

    @Query("SELECT u.active FROM Users u WHERE u.id = :id")
    Boolean isUserActive(@Param("id") String id);
}
