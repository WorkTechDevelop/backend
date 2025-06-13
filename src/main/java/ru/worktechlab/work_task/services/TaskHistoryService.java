package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.dto.task_history.TaskHistoryDto;
import ru.worktechlab.work_task.dto.request_dto.UpdateStatusRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.UpdateTaskModelDTO;
import ru.worktechlab.work_task.mappers.TaskHistoryMapper;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.TaskHistoryRepository;
import ru.worktechlab.work_task.utils.UserContext;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskHistoryService {

    private final TaskHistoryRepository repository;
    private final TaskHistoryMapper taskHistoryMapper;
    private final UserContext userContext;
    private final UserService userService;

    public void saveTaskModelChanges(TaskModel oldEntity, UpdateTaskModelDTO dto) {
        saveHistory(createTaskHistory(oldEntity, dto), oldEntity.getId());
    }

    public void saveTaskModelChanges(TaskModel oldEntity, UpdateStatusRequestDTO dto) {
        saveHistory(createTaskStatusHistory(oldEntity, dto), oldEntity.getId());
    }

    private void saveHistory(List<TaskHistoryDto> dto, String taskId) {
        if (CollectionUtils.isEmpty(dto)) return;
        User user = userService.findUserById(userContext.getUserData().getUserId());
        List<TaskHistory> histories = taskHistoryMapper.toEntity(dto, user, taskId);
        saveAll(histories);
    }

    private void saveAll(List<TaskHistory> history) {
        if (CollectionUtils.isEmpty(history)) return;
        repository.saveAll(history);
    }

    private List<TaskHistoryDto> createTaskHistory(TaskModel oldEntity, UpdateTaskModelDTO dto) {
        oldEntity.setTitleHistory(oldEntity.getTitle(), dto.getTitle());
        oldEntity.setPriorityHistory(oldEntity.getPriority(), dto.getPriority());
        oldEntity.setAssigneeHistory(oldEntity.getAssignee(), dto.getAssignee());
        oldEntity.setDescriptionHistory(oldEntity.getDescription(), dto.getDescription());
        oldEntity.setSprintIdHistory(oldEntity.getSprintId(), dto.getSprintId());
        oldEntity.setTaskTypeHistory(oldEntity.getTaskType(), dto.getTaskType());
        oldEntity.setEstimationHistory(String.valueOf(oldEntity.getEstimation()), dto.getEstimation().toString());
        oldEntity.setCodeHistory(oldEntity.getCode(), dto.getCode());
        oldEntity.setStatusHistory(oldEntity.getStatus(), dto.getStatus());
        return oldEntity.getChanges();
    }

    private List<TaskHistoryDto> createTaskStatusHistory(TaskModel oldEntity, UpdateStatusRequestDTO dto) {
        oldEntity.setStatusHistory(oldEntity.getStatus(), dto.getStatus());
        return oldEntity.getChanges();
    }
}
