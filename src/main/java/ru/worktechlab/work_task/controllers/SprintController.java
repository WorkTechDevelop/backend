package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.sprints.ActivateSprintDtoRequest;
import ru.worktechlab.work_task.dto.sprints.SprintDtoRequest;
import ru.worktechlab.work_task.dto.sprints.SprintInfoDTO;
import ru.worktechlab.work_task.exceptions.BadRequestException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.services.SprintsService;

@RestController
@RequestMapping("work-task/v1/sprint")
@RequiredArgsConstructor
@Tag(name = "Sprint", description = "Управление спринтами")
public class SprintController {
    private final SprintsService sprintsService;

    @GetMapping("/sprint-info")
    @Operation(summary = "Вывести информацию об активном спринте")
    public SprintInfoDTO getSprintInfo() throws NotFoundException {
        return sprintsService.getActiveSprint();
    }

    @PostMapping("/project/{projectId}/create")
    @Operation(summary = "Создание спринта")
    public SprintInfoDTO createSprint(@Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
                                      @PathVariable String projectId,
                                      @Parameter(description = "Данные спринта", required = true)
                                      @RequestBody SprintDtoRequest data) throws NotFoundException {
        return sprintsService.createSprint(projectId, data);
    }

    @PutMapping("/{sprintId}/activate")
    @Operation(summary = "Запуск/завершение спринта спринта")
    public SprintInfoDTO activateSprint(@Parameter(description = "ИД спринта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
                                        @PathVariable String sprintId,
                                        @Parameter(description = "Признак запуска/завершения спринта", required = true)
                                        @RequestBody ActivateSprintDtoRequest data) throws NotFoundException, BadRequestException {
        return sprintsService.activateSprint(sprintId, data);
    }
}