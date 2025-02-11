package ru.worktechlab.work_task.implementer;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ImplementerRepository {
    private final Map<Integer, String> implementers = Map.of(
            1, "Вася",
            2, "Петя",
            3, "Гриша",
            4, "Аня",
            5, "Стас");

    private String getImplementer(Integer id) {
        return implementers.get(id);
    }

}
