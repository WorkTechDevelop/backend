package ru.worktechlab.work_task.sprints;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.responseDTO.SprintInfoDTO;

@RestController
@RequestMapping("work-task/v1/sprint")
@Slf4j
@AllArgsConstructor
public class SprintController {
    private final SprintsService sprintsService;

    @GetMapping("/sprint-info")
    public ResponseEntity<SprintInfoDTO> getSprintInfo(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод информации о спринте");
        SprintInfoDTO sprintInfo = sprintsService.getSprintName(jwtToken);
        return ResponseEntity.ok(sprintInfo);
    }
}
