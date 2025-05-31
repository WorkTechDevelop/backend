package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.dto.response_dto.UsersProjectsDTO;
import ru.worktechlab.work_task.models.tables.TaskModel;
import ru.worktechlab.work_task.services.ProjectsService;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/projects")
@Slf4j
@AllArgsConstructor
@Tag(name = "Project", description = "Управление проектами")
public class ProjectsController {
    private ProjectsService projectsService;

    @GetMapping("/all-user-project")
    @Operation(summary = "Вывести список проектов пользователя")
    public List<UsersProjectsDTO> getAllUserProjects() {
        log.info("Вывод всех проектов пользователя");
        return projectsService.getAllUserProjects();
    }

    @PostMapping("/set-project/{id}")
    @Operation(summary = "Устанавливает выбранный по ID проект - основным проектом пользователя")
    public List<TaskModel> setMainProject(
            @Parameter(
                    name = "id",
                    description = "id проекта",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable String id) {

        log.info("Установить проект основным {}", id);
        return projectsService.setMainProject(id);
    }

    @GetMapping("/users-projects")
    @Operation(summary = "Вывести список всех проектов пользователя")
    public List<UsersProjectsDTO> getProjectByUser() {
        log.info("Вывод всех проектов пользователя");
        return projectsService.getUserProject();
    }

    @GetMapping("/active-project")
    @Operation(summary = "Получить ID основного проекта пользователя")
    public String getActiveProject() {
        log.info("Получить id активного проекта");
        return projectsService.getLastProjectId();
    }
}
