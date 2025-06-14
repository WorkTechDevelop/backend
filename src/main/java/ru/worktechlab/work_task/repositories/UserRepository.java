package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("FROM User WHERE email = :email AND active")
    Optional<User> findExistUserByEmail(String email);

    @Query("SELECT u.role_id FROM User u WHERE u.email = :email")
    Optional<RoleModel> findRoleByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastProjectId = :lastProjectId WHERE u.id = :id")
    void updateLastProjectIdById(@Param("id") String id, @Param("lastProjectId") String lastProjectId);

    boolean existsByEmail(String email);

    @Query("SELECT u.active FROM User u WHERE u.id = :id")
    Boolean isUserActive(@Param("id") String id);

    @Query("from User where confirmationToken = :token")
    Optional<User> findExistUserByConfirmationToken(String token);

    @Query("from User where email = :email and active and confirmedAt is not null")
    Optional<User> findActiveUserByEmail(String email);

    @Query("from User where id = :id and active and confirmedAt is not null")
    Optional<User> findActiveUserById(String id);

    @Query("from User where confirmedAt is not null")
    List<User> getUsers();

    @Query("from User where id in :userIds and confirmedAt is not null")
    Stream<User> findUsersByIdsIn(Collection<String> userIds);
}
