package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.users.UserDataDto;
import ru.worktechlab.work_task.dto.users.UserShortDataDto;
import ru.worktechlab.work_task.models.enums.Gender;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.dto.users.RegisterDTO;

import java.util.List;

@Mapper(config = MapStructConfiguration.class, uses = {RoleMapper.class})
public interface UserMapper {

    UserShortDataDto toShortDataDto(User user);

    List<UserShortDataDto> toShortDataList(List<User> users);

    @Mapping(source = "id", target = "userId")
    @Mapping(target = "gender", source = "gender", qualifiedByName = "genderDescription")
    UserDataDto toUserFullData(User user);

    @Named("genderDescription")
    default String getGenderDescription(Gender gender) {
        return gender.getDescription();
    }
}
