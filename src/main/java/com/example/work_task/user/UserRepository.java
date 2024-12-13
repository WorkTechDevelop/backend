package com.example.work_task.user;

import com.example.work_task.model.RoleModel;
import com.example.work_task.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u.role.name FROM Users u WHERE u.email = :email")
    Optional<RoleModel> findRoleByEmail(@Param("email") String email);
}
