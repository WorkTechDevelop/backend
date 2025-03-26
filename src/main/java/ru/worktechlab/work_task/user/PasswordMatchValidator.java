package ru.worktechlab.work_task.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.worktechlab.work_task.model.rest.RegisterDto;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterDto> {

    @Override
    public boolean isValid(RegisterDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("Пароли не совпадают")
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
