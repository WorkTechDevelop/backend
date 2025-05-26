package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<UsersProjectsDTO>> getAllUserProjects(
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {

        log.info("Вывод всех проектов пользователя");
        List<UsersProjectsDTO> projects = projectsService.getAllUserProjects(jwtToken);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/set-project/{id}")
    @Operation(summary = "Устанавливает выбранный по ID проект - основным проектом пользователя")
    public ResponseEntity<List<TaskModel>> setMainProject(
            @Parameter(
                    name = "id",
                    description = "id проекта",
                    required = true,
                    example = "507f1f77bcf86cd799439011",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathVariable String id,
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {

        log.info("Установить проект основным {}", id);
        List<TaskModel> tasks = projectsService.setMainProject(id, jwtToken);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users-projects")
    @Operation(summary = "Вывести список всех проектов пользователя")
    public ResponseEntity<List<UsersProjectsDTO>> getProjectByUser(
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех проектов пользователя");
        List<UsersProjectsDTO> projects = projectsService.getUserProject(jwtToken);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/active-project")
    @Operation(summary = "Получить ID основного проекта пользователя")
    public ResponseEntity<String> getActiveProject(
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Получить id активного проекта");
        String activeProject = projectsService.getLastProjectId(jwtToken);
        return ResponseEntity.ok(activeProject);
    }
}
