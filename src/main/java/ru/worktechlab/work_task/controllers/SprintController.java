package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.dto.sprints.SprintInfoDTO;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.services.SprintsService;

@RestController
@RequestMapping("work-task/v1/sprint")
@AllArgsConstructor
@Tag(name = "Sprint", description = "Управление спринтами")
public class SprintController {
    private final SprintsService sprintsService;

    @GetMapping("/sprint-info")
    @Operation(summary = "Вывести информацию об активном спринте")
    public SprintInfoDTO getSprintInfo() throws NotFoundException {
        return sprintsService.getActiveSprint();
    }
}