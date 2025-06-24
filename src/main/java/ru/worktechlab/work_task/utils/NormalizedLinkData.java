package ru.worktechlab.work_task.utils;

import ru.worktechlab.work_task.models.tables.LinkType;
import ru.worktechlab.work_task.models.tables.TaskModel;

public record NormalizedLinkData (TaskModel master, TaskModel slave, LinkType linkType) {}
