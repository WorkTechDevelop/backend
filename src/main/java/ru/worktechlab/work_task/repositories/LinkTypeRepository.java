package ru.worktechlab.work_task.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.worktechlab.work_task.models.enums.LinkTypeName;
import ru.worktechlab.work_task.models.tables.LinkType;

import java.util.Optional;

public interface LinkTypeRepository extends JpaRepository<LinkType, Long> {
    Optional<LinkType> findById(Integer id);

    Optional<LinkType> findByName(LinkTypeName name);
}