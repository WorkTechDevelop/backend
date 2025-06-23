package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.worktechlab.work_task.config.MapStructConfiguration;
import ru.worktechlab.work_task.dto.task_comment.CommentResponseDto;
import ru.worktechlab.work_task.dto.task_comment.GetAllTasksCommentsResponseDto;
import ru.worktechlab.work_task.models.tables.Comment;

import java.util.List;

@Mapper(config = MapStructConfiguration.class, uses = UserMapper.class)
public interface CommentMapper {

    @Mapping(source = "id", target = "commentId")
    CommentResponseDto toDto(Comment comment);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "user", target = "user")
    GetAllTasksCommentsResponseDto toGetAllDto(Comment comment);

    List<GetAllTasksCommentsResponseDto> toGetAllDtoList(List<Comment> comments);
}
