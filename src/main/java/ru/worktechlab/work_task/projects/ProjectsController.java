package ru.worktechlab.work_task.projects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.model.db.Projects;
import ru.worktechlab.work_task.model.db.TaskModel;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("work-task/v1/projects")
@Slf4j
@AllArgsConstructor
public class ProjectsController {
    private ProjectsService projectsService;

    @GetMapping("/all-user-project")
    public ResponseEntity<List<Projects>> getAllUserProjects(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех проектов пользователя");

        try {
            List<Projects> projects = projectsService.getAllUserProjects(jwtToken);
            return ResponseEntity.ok(projects);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении проектов: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/set-project/{id}")
    public ResponseEntity<List<TaskModel>> setMainProject(
            @PathVariable String id,
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Установить проект основным {}", id);
        try {
            List<TaskModel> tasks = projectsService.setMainProject(id, jwtToken);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            log.error("Ошибка при выборе проекта и получении задач: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}
