package ru.worktechlab.work_task.dto.projects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.worktechlab.work_task.dto.statuses.ProjectStatusDto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProjectDto {
    @NotNull
    @Schema(description = "ИД проекта")
    private String id;
    @NotNull
    @Schema(description = "Название проекта")
    private String name;
    @NotNull
    @Schema(description = "Владелец проекта")
    private String owner;
    @NotNull
    @Schema(description = "Дата создания проекта")
    private Date creationDate;
    @Schema(description = "Дата закрытия проекта")
    private Date finishDate;
    @NotNull
    @Schema(description = "Дата начала проекта")
    private Date startDate;
    @Schema(description = "Дата обновления проекта")
    private Date updateDate;
    @Schema(description = "Комментарий к проекту")
    private String description;
    @NotNull
    @Schema(description = "Флаг активности проекта")
    private boolean active;
    @NotNull
    @Schema(description = "Создатель проекта")
    private String creator;
    @Schema(description = "Пользователь, закрывший проект")
    private String finisher;
    @NotNull
    @Schema(description = "Код проекта")
    private String code;
    @Schema(description = "Статусы проекта")
    private List<ProjectStatusDto> statuses = new ArrayList<>();
}
