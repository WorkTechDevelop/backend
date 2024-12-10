package com.example.work_task.user;

import com.example.work_task.model.RoleModel;
import com.example.work_task.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT user.role FROM Users user LEFT JOIN FETCH user.role.users WHERE user.email = :email")
    Optional<RoleModel> findRoleByEmail(@Param("email") String email);
}
