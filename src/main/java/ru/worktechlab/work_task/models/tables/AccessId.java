package ru.worktechlab.work_task.models.tables;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class AccessId implements Serializable {
    private String userId;
    private Integer roleId;

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccessId that = (AccessId) obj;
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
    }
}
