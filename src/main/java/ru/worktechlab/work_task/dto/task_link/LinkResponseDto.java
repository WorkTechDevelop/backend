package ru.worktechlab.work_task.dto.task_link;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkResponseDto {
    private String linkId;

    public LinkResponseDto(String linkId) {
        this.linkId = linkId;
    }
}