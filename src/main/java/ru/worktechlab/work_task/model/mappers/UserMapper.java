package ru.worktechlab.work_task.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.rest.RegisterDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role_id", source = "role.id")
    @Mapping(target = "lastProjectId", ignore = true)
    Users registerDtoToUser(RegisterDto dto, RoleModel role);
}
