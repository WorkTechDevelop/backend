package ru.worktechlab.work_task.dto.task_history;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskHistoryResponseDto {
    private String userId;
    private String userFullName;
    private String fieldName;
    private String initialValue;
    private String newValue;
    private LocalDateTime createdAt;
}
