package ru.worktechlab.work_task.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.Users;
import ru.worktechlab.work_task.models.request_dto.RegisterDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role_id", source = "role.id")
    @Mapping(target = "lastProjectId", ignore = true)
    Users registerDtoToUser(RegisterDTO dto, RoleModel role);
}
