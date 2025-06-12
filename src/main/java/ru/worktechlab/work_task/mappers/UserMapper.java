package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.dto.request_dto.RegisterDTO;

@Mapper(config = MapStructConfiguration.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "false")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role_id", source = "role.id")
    @Mapping(target = "lastProjectId", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "confirmationToken", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    User registerDtoToUser(RegisterDTO dto, RoleModel role);
}
