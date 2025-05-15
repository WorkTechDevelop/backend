package ru.worktechlab.work_task.utils;

import org.springframework.stereotype.Component;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.models.tables.Users;
import ru.worktechlab.work_task.models.mappers.TaskModelMapper;
import ru.worktechlab.work_task.models.response_dto.TaskModeResponselDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
public class TaskModelConverter {
    private final TaskModelMapper taskModelMapper;

    public TaskModelConverter(TaskModelMapper taskModelMapper) {
        this.taskModelMapper = taskModelMapper;
    }

    public List<TaskModeResponselDTO> convertTasks(List<TaskModel> tasks, Map<String, Users> usersById) {
        return tasks.stream()
                .map(task -> {
                    Users creator = usersById.get(task.getCreator());
                    Users assignee = usersById.get(task.getAssignee());
                    return taskModelMapper.taskModelResponseFromEntity(task, creator, assignee);
                })
                .collect(Collectors.toList());
    }
}
