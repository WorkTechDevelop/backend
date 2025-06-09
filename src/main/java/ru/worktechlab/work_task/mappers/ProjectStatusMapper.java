package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.statuses.ProjectStatusDto;
import ru.worktechlab.work_task.models.tables.ProjectStatus;

@Mapper(config = MapStructConfiguration.class)
public interface ProjectStatusMapper {

    @Mapping(source = "project.id", target = "projectId")
    ProjectStatusDto todo(ProjectStatus status);
}
