package ru.worktechlab.work_task.models.enums;

import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
@Getter
public enum Roles {
    @FieldNameConstants.Include ADMIN,
    @FieldNameConstants.Include PROJECT_OWNER,
    @FieldNameConstants.Include PROJECT_MEMBER,
    @FieldNameConstants.Include POWER_USER
}
