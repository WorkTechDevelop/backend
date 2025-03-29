package ru.worktechlab.work_task.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.rest.TaskModelDTO;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TaskModelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "TODO")
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "creationDate", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "updateDate", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    TaskModel toEntity(TaskModelDTO dto);

    default TaskModel toEntity(TaskModelDTO dto, String creatorGuid) {
        TaskModel task = toEntity(dto);
        task.setId(UUID.randomUUID().toString());
        task.setCreator(creatorGuid);
        return task;
    }
}
