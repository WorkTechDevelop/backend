package ru.worktechlab.work_task.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GenderValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {
    String message() default "Некорректное значение для gender. Допустимые значения: MALE, FEMALE, OTHER";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
