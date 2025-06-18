package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryDto;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryResponseDto;
import ru.worktechlab.work_task.mappers.TaskHistoryMapper;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.TaskHistoryRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskHistoryService {

    private final TaskHistoryRepository repository;
    private final UserContext userContext;
    private final UserService userService;
    private final TaskHistoryMapper taskHistoryMapper;

    public void saveTaskModelChanges(TaskModel oldTask, UpdateTaskModelDTO dto) {
        saveHistory(createTaskHistory(oldTask, dto), oldTask.getId());
    }

    public void saveTaskModelChanges(TaskModel oldTask, UpdateStatusRequestDTO dto) {
        saveHistory(createTaskStatusHistory(oldTask, dto), oldTask.getId());
    }

    private void saveHistory(List<TaskHistoryDto> dto, String taskId) {
        if (CollectionUtils.isEmpty(dto)) return;
        User user = userService.findActiveUserById(userContext.getUserData().getUserId());
        List<TaskHistory> histories = convertToEntity(dto, user, taskId);
        saveAll(histories);
    }

    private void saveAll(List<TaskHistory> history) {
        if (CollectionUtils.isEmpty(history)) return;
        repository.saveAll(history);
    }

    private List<TaskHistoryDto> createTaskHistory(TaskModel oldTask, UpdateTaskModelDTO dto) {
        oldTask.setTitleHistory(dto.getTitle());
        oldTask.setPriorityHistory(dto.getPriority());
        oldTask.setAssigneeHistory(dto.getAssignee());
        oldTask.setDescriptionHistory(dto.getDescription());
        oldTask.setSprintIdHistory(dto.getSprintId());
        oldTask.setTaskTypeHistory(dto.getTaskType());
        oldTask.setEstimationHistory(String.valueOf(dto.getEstimation()));
        oldTask.setStatusHistory(dto.getStatus());
        return oldTask.getChanges();
    }

    private List<TaskHistoryDto> createTaskStatusHistory(TaskModel oldTask, UpdateStatusRequestDTO dto) {
        oldTask.setStatusHistory(dto.getStatus());
        return oldTask.getChanges();
    }

    private List<TaskHistory> convertToEntity(List<TaskHistoryDto> dtos, User user, String taskId) {
        if (CollectionUtils.isEmpty(dtos)) {
            return java.util.Collections.emptyList();
        }
        return dtos.stream()
                .map(dto -> new TaskHistory(taskId,
                        dto.getFieldName(),
                        dto.getInitialValue(),
                        dto.getNewValue(),
                        user)
                )
                .toList();
    }

    public List<TaskHistoryResponseDto> getTaskHistoryById(String taskId) {
        List<TaskHistory> taskHistories = repository.findAllByTaskIdOrderByCreatedAtDesc(taskId);
        return taskHistoryMapper.convertToDto(taskHistories);
    }
}
