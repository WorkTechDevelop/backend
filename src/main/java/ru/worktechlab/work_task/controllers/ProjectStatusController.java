package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.statuses.ProjectStatusDto;
import ru.worktechlab.work_task.dto.statuses.ProjectStatusRequestDto;
import ru.worktechlab.work_task.dto.statuses.StatusListResponseDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.services.ProjectStatusService;

@RestController
@RequestMapping("work-task/v1/status")
@RequiredArgsConstructor
@Tag(name = "Status", description = "Управление статусами")
public class ProjectStatusController {

    private final ProjectStatusService projectStatusService;

    @GetMapping("/project/{projectId}/statuses")
    @Operation(summary = "Список статусов проекта")
    public StatusListResponseDto getStatuses(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId
    ) throws NotFoundException {
        return projectStatusService.getStatuses(projectId);
    }

    @PostMapping("/project/{projectId}/create-status")
    @Operation(summary = "Создание статуса")
    public ProjectStatusDto createStatus(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId,
            @RequestBody @Valid ProjectStatusRequestDto data
    ) throws NotFoundException {
        return projectStatusService.createStatus(projectId, data);
    }

    @PutMapping("/project/{projectId}/status/{statusId}/update-status")
    @Operation(summary = "Обновление данных статуса")
    public ProjectStatusDto updateStatus(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId,
            @Parameter(description = "ИД статуса", example = "123", required = true)
            @PathVariable long statusId,
            @RequestBody @Valid ProjectStatusRequestDto data
    ) throws NotFoundException {
        return projectStatusService.updateStatus(projectId, statusId, data);
    }
}
