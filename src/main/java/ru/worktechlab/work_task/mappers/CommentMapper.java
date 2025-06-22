package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.task_comment.CommentResponseDto;
import ru.worktechlab.work_task.models.tables.Comment;

@Mapper(config = MapStructConfiguration.class)
public interface CommentMapper {

    @Mapping(source = "id", target = "commentId")
    CommentResponseDto toDto(Comment comment);
}
