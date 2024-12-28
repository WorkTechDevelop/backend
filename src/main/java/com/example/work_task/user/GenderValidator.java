package com.example.work_task.user;

import com.example.work_task.model.db.enums.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return Arrays.stream(Gender.values())
                .anyMatch(gender -> gender.name().equalsIgnoreCase(value));
    }

    }
