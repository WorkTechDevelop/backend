package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.OkResponse;
import ru.worktechlab.work_task.dto.StringIdsDto;
import ru.worktechlab.work_task.dto.projects.*;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.services.ProjectsService;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "Управление проектами")
public class ProjectsController {
    private final ProjectsService projectsService;

    @GetMapping("/all-user-project")
    @Operation(summary = "Вывести список проектов пользователя")
    public List<ShortProjectDataDto> getAllUserProjects() {
        return projectsService.getAllUserProjects();
    }

    @GetMapping("/active-project")
    @Operation(summary = "Получить ID основного проекта пользователя")
    public String getActiveProject() {
        return projectsService.getLastProjectId();
    }

    @PostMapping("/create-project")
    @Operation(summary = "Создание проекта")
    public ProjectDto createProject(
            @RequestBody @Valid ProjectRequestDto data
    ) {
        return projectsService.createProject(data);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Получение данных проекта по ИД")
    public ProjectDto getProjectData(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId
    ) throws NotFoundException {
        return projectsService.getProjectData(projectId);
    }

    @PostMapping("/{projectId}")
    @Operation(summary = "Получение данных проекта по ИД и фильтру")
    public ProjectDataDto getProjectDataByFilter(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId,
            @Parameter(description = "Данные фильтра")
            @RequestBody @Valid ProjectDataFilterDto filter
    ) throws NotFoundException {
        return projectsService.getProjectDataByFilter(projectId, filter);
    }

    @PutMapping("/finish-project/{projectId}")
    @Operation(summary = "Завершение проекта по ИД")
    public ProjectDto finishProject(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId
    ) throws NotFoundException {
        return projectsService.finishProject(projectId);
    }

    @PutMapping("/start-project/{projectId}")
    @Operation(summary = "Запуск проекта по ИД")
    public ProjectDto startProject(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId
    ) throws NotFoundException {
        return projectsService.startProject(projectId);
    }

    @PutMapping("/{projectId}/add-users")
    @Operation(summary = "Добавление проекта пользователям")
    public OkResponse addProjectForUsers(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId,
            @Parameter(description = "Идентификаторы пользователей", example = "[\"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", \"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", ...]")
            @RequestBody StringIdsDto data
    ) throws NotFoundException {
        return projectsService.addProjectForUsers(projectId, data);
    }

    @DeleteMapping("/{projectId}/delete-users")
    @Operation(summary = "Удаление пользователей из проекта")
    public OkResponse deleteProjectForUsers(
            @Parameter(description = "ИД проекта", example = "656c989e-ceb1-4a9f-a6a9-9ab40cc11540", required = true)
            @PathVariable String projectId,
            @Parameter(description = "Идентификаторы пользователей", example = "[\"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", \"656c989e-ceb1-4a9f-a6a9-9ab40cc11540\", ...]")
            @RequestBody StringIdsDto data
    ) throws NotFoundException {
        return projectsService.deleteProjectForUsers(projectId, data);
    }
}
