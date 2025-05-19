package ru.worktechlab.work_task.dto.response_dto;

import lombok.Data;

import java.sql.Date;

@Data
public class SprintInfoDTO {
    private String sprintName;
    private java.sql.Date startDate;
    private Date endDate;

    public SprintInfoDTO(String sprintName, Date startDate, Date endDate) {
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
