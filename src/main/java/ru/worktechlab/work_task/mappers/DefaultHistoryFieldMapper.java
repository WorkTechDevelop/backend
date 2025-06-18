package ru.worktechlab.work_task.mappers;

import org.springframework.stereotype.Component;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryResponseDto;
import ru.worktechlab.work_task.dto.users.UserShortDataDto;
import ru.worktechlab.work_task.interfaces.HistoryFieldMapper;
import ru.worktechlab.work_task.models.tables.TaskHistory;

@Component
public class DefaultHistoryFieldMapper implements HistoryFieldMapper {
    private static final String ASSIGNEE_FIELD = "Исполнитель";
    private final UserMapper userMapper;

    public DefaultHistoryFieldMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean supports(String fieldName) {
        return !ASSIGNEE_FIELD.equals(fieldName);
    }

    @Override
    public TaskHistoryResponseDto map(TaskHistory h) {
        UserShortDataDto userDto = userMapper.toShortDataDto(h.getUser());
        return TaskHistoryResponseDto.builder()
                .user(userDto)
                .fieldName(h.getFieldName())
                .initialValue(h.getInitialValue())
                .newValue(h.getNewValue())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
