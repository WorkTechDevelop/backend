package com.example.work_task.servicedb;

import com.example.work_task.model.TaskModel;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.work_task.tools.awaiter.AwaitUtils.waitUtilDbAction;
@AllArgsConstructor
public class DbService {
    private final QuerryCollection qc;

    public TaskModel getTaskByTitle(String title) {
        return getTaskDataRow(title, TaskModel::getTitle);
    }

    public TaskModel getTaskByImplementors(String implementors) {
        return getTaskDataRow(implementors, TaskModel::getImplementer);
    }

    public TaskModel getTaskByStatus(String status) {
        return getTaskDataRow(status, TaskModel::getStatus);
    }


    @FunctionalInterface
    public interface FieldExtractor<T, R> {
        R extract(T t);
    }

    /**
     * Извлечение строки данных о статусах процесса в WF
     * @param expectedValue - ожидаемое значение поля
     * @param field - динамическое лямбда-варажение - поле, содержащее оижаемое значение
     */

    public TaskModel getTaskDataRow(String expectedValue, FieldExtractor<TaskModel, String> field) {
        return qc.getAllTaskDataRow()
                        .stream()
                        .filter(dr -> field.extract(dr).contains(expectedValue))
                        .findFirst()
                        .orElse(null);
    }

    public List<TaskModel> getListTaskDataRow(String expectedValue, FieldExtractor<TaskModel, String> field) {
        return qc.getAllTaskDataRow()
                        .stream()
                        .filter(dr -> field.extract(dr).contains(expectedValue))
                        .collect(Collectors.toList());
    }
}
