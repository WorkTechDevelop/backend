package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.users.UserShortDataDto;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.dto.request_dto.RegisterDTO;

import java.util.List;

@Mapper(config = MapStructConfiguration.class)
public interface UserMapper {

    UserShortDataDto toShortDataDto(User user);

    List<UserShortDataDto> toShortDataList(List<User> users);
}
