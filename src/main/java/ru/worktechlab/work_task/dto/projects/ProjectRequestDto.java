package ru.worktechlab.work_task.dto.projects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProjectRequestDto {
    @NotNull
    @Size(max = 50, message = "Длинна поля name не может привышать 50 символов")
    @Schema(description = "Название проекта")
    private String name;

    @Schema(description = "Комментарий к проекту")
    private String description;

    @NotNull
    @Size(max = 10, message = "Длинна поля code не может привышать 10 символов")
    @Schema(description = "Код проекта")
    private String code;

    @Schema(description = "Признак активности проекта")
    private boolean active;
}
