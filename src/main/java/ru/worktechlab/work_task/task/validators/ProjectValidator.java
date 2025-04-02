package ru.worktechlab.work_task.task.validators;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.worktechlab.work_task.projects.ProjectRepository;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateProjectExist(String value) {
        boolean isActive = isActive(value);
        if (!isActive) {
            throw new EntityNotFoundException(
                    String.format("Проект с id: %s закрыт или не существует", value)
            );
        }
    }

    public boolean isActive(String value) {
        if (value == null) return false;
        Boolean isActive = projectRepository.isProjectActive(value);
        return Boolean.TRUE.equals(isActive);
    }


}
