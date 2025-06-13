package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryDto;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryResponseDto;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.models.tables.User;

import java.util.List;

@Mapper(config = MapStructConfiguration.class)
public interface TaskHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", expression = "java(taskId)")
    @Mapping(target = "user", expression = "java(user)")
    @Mapping(target = "dateTime", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    TaskHistory toEntity(TaskHistoryDto dto, User user, String taskId);

    default List<TaskHistory> toEntity(List<TaskHistoryDto> dtos, User user, String taskId) {
        if (dtos == null) {
            return java.util.Collections.emptyList();
        }
        return dtos.stream()
                .map(dto -> toEntity(dto, user, taskId))
                .toList();
    }

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(getFullName(taskHistory.getUser()))")
    TaskHistoryResponseDto toDto(TaskHistory taskHistory);

    default String getFullName(User user) {
        if (user == null) return "";
        StringBuilder fullName = new StringBuilder();
        if (user.getFirstName() != null) fullName.append(user.getFirstName()).append(" ");
        if (user.getMiddleName() != null) fullName.append(user.getMiddleName()).append(" ");
        if (user.getLastName() != null) fullName.append(user.getLastName());
        return fullName.toString().trim();
    }

    default List<TaskHistoryResponseDto> toDto(List<TaskHistory> entities) {
        if (entities == null) {
            return java.util.Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}