package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.projects.ProjectDto;
import ru.worktechlab.work_task.dto.projects.ShortProjectDataDto;
import ru.worktechlab.work_task.models.tables.Project;

import java.util.List;

@Mapper(config = MapStructConfiguration.class, uses = {UserMapper.class, TaskStatusMapper.class})
public interface ProjectMapper {

    List<ShortProjectDataDto> toShortDataDto(List<Project> projects);

    ProjectDto toProjectDto(Project project);
}
