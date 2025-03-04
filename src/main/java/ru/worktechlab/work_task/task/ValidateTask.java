package ru.worktechlab.work_task.task;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import io.micrometer.common.util.StringUtils;
import ru.worktechlab.work_task.model.db.Projects;
import ru.worktechlab.work_task.model.db.Sprints;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.model.db.enums.Priority;
import ru.worktechlab.work_task.model.db.enums.TaskType;
import ru.worktechlab.work_task.projects.ProjectRepository;
import ru.worktechlab.work_task.sprints.SprintsRepository;

import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@Component
public class ValidateTask {
    private SprintsRepository sprintsRepository;
    private ProjectRepository projectRepository;

    private static final String ERROR_TITLE_FORMAT = "Некорректный формат поля TITLE";
    private static final String ERROR_DESCRIPTION_FORMAT = "Некорректный формат поля DESCRIPTION";
    private static final String ERROR_SPRINT_NOT_FOUND_OR_CLOSED = "Спринт закрыт или не существует";
    private static final String ERROR_ESTIMATION_FORMAT = "Некорректный формат поля ESTIMATION";
    private static final String ERROR_PRIORITY_VALUE = "Некорректное значение поля PRIORITY";
    private static final String ERROR_TASK_TYPE_VALUE = "Некорректное значение поля TASK_TYPE";
    private static final String ERROR_PROJECT_ID_VALUE = "Проект закрыт или не существует";
    public List<String> errors;

    public List<String> validateTask(TaskModel taskModel) {
        validateTitle(taskModel);
        validateDescription(taskModel);
        validateSprintId(taskModel);
        validateEstimation(taskModel);
        validatePriority(taskModel);
        validateTaskType(taskModel);
        validateProjectId(taskModel);
        return errors;
    }

    private void validateProjectId(TaskModel taskModel) {
        if (taskModel.getSprintId() != null) {
            Projects projects = findProjectById(taskModel.getProjectId());
            if (!projects.isActive()) {
                errors.add(ERROR_PROJECT_ID_VALUE);
            }
        } else  {
            errors.add(ERROR_PROJECT_ID_VALUE);
        }
    }

    private Projects findProjectById(String projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    private void validateTitle(TaskModel taskModel) {
        if (StringUtils.isBlank(taskModel.getTitle())) {
            errors.add(ERROR_TITLE_FORMAT);
        } else if (taskModel.getTitle().length() > 255) {
            errors.add(ERROR_TITLE_FORMAT);
        }
    }

    private void validateDescription(TaskModel taskModel) {
        if (taskModel.getDescription() != null && taskModel.getDescription().length() > 4096) {
            errors.add(ERROR_DESCRIPTION_FORMAT);
        }
    }

    private void validateSprintId(TaskModel taskModel) {
        if (taskModel.getSprintId() != null) {
            Sprints sprint = findSprintById(taskModel.getSprintId());
            if (sprint == null || !sprint.isActive()) {
                errors.add(ERROR_SPRINT_NOT_FOUND_OR_CLOSED);
            }
        }
    }

    private Sprints findSprintById(String sprintId) {
        return sprintsRepository.findById(sprintId).orElse(null);
    }

    private void validateEstimation(TaskModel taskModel) {
        if (taskModel.getEstimation() == null) {
            errors.add(ERROR_ESTIMATION_FORMAT);
        }
    }

    private void validatePriority(TaskModel taskModel) {
        if (isValueInEnum(Priority.class, taskModel.getPriority())) {
            errors.add(ERROR_PRIORITY_VALUE);
        }
    }

    private void validateTaskType(TaskModel taskModel) {
        if (isValueInEnum(TaskType.class, taskModel.getTaskType())) {
            errors.add(ERROR_TASK_TYPE_VALUE);
        }
    }

    private <T extends Enum<T>> boolean isValueInEnum(Class<T> enamClass, String value) {
        return Stream.of(enamClass.getEnumConstants())
                .noneMatch(e -> e.name().equals(value));
    }
}
