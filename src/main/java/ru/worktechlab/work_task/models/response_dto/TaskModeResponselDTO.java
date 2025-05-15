package ru.worktechlab.work_task.models.response_dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TaskModeResponselDTO {

    private String id;

    private String title;

    private String description;

    private String priority;

    private String creator;

    private String assignee;

    private String projectId;

    private String sprintId;

    private String taskType;

    private Integer estimation;

    private String status;

    private Timestamp creationDate;

    private Timestamp updateDate;

    private String code;
}
