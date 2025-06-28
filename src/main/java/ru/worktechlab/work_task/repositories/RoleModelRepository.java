package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.worktechlab.work_task.models.enums.Roles;
import ru.worktechlab.work_task.models.tables.RoleModel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleModelRepository extends JpaRepository<RoleModel, Integer> {
    Optional<RoleModel> findByName(Roles name);

    @Query("from RoleModel where id in :roleIds")
    List<RoleModel> findRolesByIdsIn(Collection<String> roleIds);

    @Modifying
    @Query(nativeQuery = true,
            value = "delete from user_role where user_id = :userId")
    void deleteUserRolesByUserId(String userId);

    @Modifying
    @Query(nativeQuery = true,
            value = "insert into user_role (id, user_id, role_id) " +
                    "values(:id, :userId, :roleId)")
    void createUserRole(String id, String userId, String roleId);

    @Modifying
    @Query(
            nativeQuery = true,
            value = "insert into user_role (id, user_id, role_id) " +
                    "values(:id, :userId, :roleId) " +
                    "on conflict on constraint unique_role_user do nothing")
    void createOrUpdateUserRole(String id, String userId, String roleId);
}
