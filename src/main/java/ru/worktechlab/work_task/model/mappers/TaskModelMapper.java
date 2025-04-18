package ru.worktechlab.work_task.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.rest.TaskModelDTO;
import ru.worktechlab.work_task.model.rest.UpdateTaskModelDTO;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TaskModelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "TODO")
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "creationDate", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "updateDate", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "sprintId", expression = "java(normalizeSprintId(dto.getSprintId()))")
    TaskModel toEntity(TaskModelDTO dto);

    default TaskModel toEntity(TaskModelDTO dto, String creatorGuid, String code) {
        TaskModel task = toEntity(dto);
        task.setId(UUID.randomUUID().toString());
        task.setCreator(creatorGuid);
        task.setCode(code);
        return task;
    }

    default String normalizeSprintId(String sprintId) {
        return (sprintId == null || sprintId.isBlank()) ? null : sprintId;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "code", ignore = true)
    void updateTaskFromDto(UpdateTaskModelDTO dto, @MappingTarget TaskModel task);

}
