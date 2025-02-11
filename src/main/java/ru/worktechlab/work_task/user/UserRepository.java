package ru.worktechlab.work_task.user;

import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.role_id FROM Users u WHERE u.email = :email")
    Optional<RoleModel> findRoleByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
}
