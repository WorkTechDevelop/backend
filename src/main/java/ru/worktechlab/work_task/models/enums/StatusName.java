package ru.worktechlab.work_task.models.enums;

import lombok.Getter;

@Getter
public enum StatusName {
    TODO("сделать", 1, true),
    IN_PROGRESS("в работе", 2, true),
    REVIEW("ревью", 3, true),
    DONE("готово", 4, true),
    CANCELED("отменена", 5, false),
    ;

    private final String description;
    private final int priority;
    private final boolean viewed;

    StatusName(String description, int priority, boolean viewed) {
        this.description = description;
        this.priority = priority;
        this.viewed = viewed;
    }
}
