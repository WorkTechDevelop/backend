package ru.worktechlab.work_task.projects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.worktechlab.work_task.model.db.TaskModel;
import ru.worktechlab.work_task.responseDTO.UsersProjectsDTO;

import java.util.List;

@RestController
@RequestMapping("work-task/v1/projects")
@Slf4j
@AllArgsConstructor
public class ProjectsController {
    private ProjectsService projectsService;

    @GetMapping("/all-user-project")
    public ResponseEntity<List<UsersProjectsDTO>> getAllUserProjects(
            @RequestHeader("Authorization") String jwtToken) {

        log.info("Вывод всех проектов пользователя");
        List<UsersProjectsDTO> projects = projectsService.getAllUserProjects(jwtToken);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/set-project/{id}")
    public ResponseEntity<List<TaskModel>> setMainProject(
            @PathVariable String id,
            @RequestHeader("Authorization") String jwtToken) {

        log.info("Установить проект основным {}", id);
        List<TaskModel> tasks = projectsService.setMainProject(id, jwtToken);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/users-projects")
    public ResponseEntity<List<UsersProjectsDTO>> getProjectByUser(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод всех проектов пользователя");
        List<UsersProjectsDTO> projects = projectsService.getUserProject(jwtToken);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/active-project")
    public ResponseEntity<String> getActiveProject(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Получить id активного проекта");
        String activeProject = projectsService.getLastProjectId(jwtToken);
        return ResponseEntity.ok(activeProject);
    }
}
