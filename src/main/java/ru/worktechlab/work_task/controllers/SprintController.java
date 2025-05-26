package ru.worktechlab.work_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.worktechlab.work_task.dto.response_dto.SprintInfoDTO;
import ru.worktechlab.work_task.services.SprintsService;

@RestController
@RequestMapping("work-task/v1/sprint")
@Slf4j
@AllArgsConstructor
@Tag(name = "Sprint", description = "Управление спринтами")
public class SprintController {
    private final SprintsService sprintsService;

    @GetMapping("/sprint-info")
    @Operation(summary = "Вывести информацию об активном спринте")
    public ResponseEntity<SprintInfoDTO> getSprintInfo(
            @Parameter(
                    name = "Authorization",
                    description = "JWT токен в формате 'Bearer {token}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String jwtToken) {
        log.info("Вывод информации о спринте");
        SprintInfoDTO sprintInfo = sprintsService.getSprintName(jwtToken);
        return ResponseEntity.ok(sprintInfo);
    }
}
