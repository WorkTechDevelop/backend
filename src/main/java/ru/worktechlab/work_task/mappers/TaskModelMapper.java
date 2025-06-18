package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.response_dto.TaskModeResponselDTO;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;

@Mapper(config = MapStructConfiguration.class)
public interface TaskModelMapper {

    default String normalizeSprintId(String sprintId) {
        return (sprintId == null || sprintId.isBlank()) ? null : sprintId;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "taskChangeDetector", ignore = true)
    @Mapping(target = "changes", ignore = true)
    @Mapping(target = "titleHistory", ignore = true)
    @Mapping(target = "priorityHistory", ignore = true)
    @Mapping(target = "assigneeHistory", ignore = true)
    @Mapping(target = "descriptionHistory", ignore = true)
    @Mapping(target = "sprintIdHistory", ignore = true)
    @Mapping(target = "taskTypeHistory", ignore = true)
    @Mapping(target = "estimationHistory", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    void updateTaskFromDto(UpdateTaskModelDTO dto, @MappingTarget TaskModel task);

    TaskModeResponselDTO taskModelResponseFromEntity(TaskModel task);

    default TaskModeResponselDTO taskModelResponseFromEntity(TaskModel task, User creator, User assignee) {
        TaskModeResponselDTO dto = taskModelResponseFromEntity(task);
        dto.setCreator(formatName(creator));
        dto.setAssignee(assignee != null ? formatName(assignee) : null);
        return dto;
    }

    private static String formatName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }
}
