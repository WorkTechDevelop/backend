package ru.worktechlab.work_task.listeners;

import jakarta.persistence.PreUpdate;
import ru.worktechlab.work_task.interfaces.EntityChangeDetector;
import ru.worktechlab.work_task.interfaces.TrackableEntity;
import ru.worktechlab.work_task.models.tables.TaskHistory;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.utils.TaskChangeDetector;
import ru.worktechlab.work_task.utils.TaskHistorySaver;

import java.util.List;

public class TrackableEntityListener {

    private final java.util.Map<Class<?>, EntityChangeDetector<?>> detectors = java.util.Map.of(
            TaskModel.class, new TaskChangeDetector()
    );

    @PreUpdate
    public void onPreUpdate(Object entity) {
        if (!(entity instanceof TrackableEntity<?> trackable)) return;

        Object previous = trackable.getPreviousState();
        if (previous == null) return;

        EntityChangeDetector detector = detectors.get(entity.getClass());
        if (detector == null) return;

        List<TaskHistory> changes = detector.detectChanges(previous, entity);
        TaskHistorySaver.saveAll(changes);
    }
}
