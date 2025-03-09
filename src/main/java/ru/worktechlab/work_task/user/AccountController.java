package ru.worktechlab.work_task.user;

import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.UserBuilder;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.db.enums.Roles;
import ru.worktechlab.work_task.model.rest.RegisterDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleModelRepository roleModelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registry")
    public ResponseEntity<?> registry(@Valid @RequestBody RegisterDto registerDto) {
        try {
            validateRegistrationData(registerDto);
            RoleModel defaultRole = getDefaultRole();
            Users user = buildUser(registerDto, defaultRole);
            userRepository.save(user);
            return new ResponseEntity<>("Пользователь успешно зарегестрирован", HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Users buildUser(RegisterDto registerDto, RoleModel role) {
        return new UserBuilder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .middleName(registerDto.getMiddleName())
                .phone(registerDto.getPhone())
                .gender(registerDto.getGender())
                .birthDate(registerDto.getBirthDate())
                .active(false)
                .roleId(role.getId())
                .lastProjectId(null)
                .build();
    }

    private RoleModel getDefaultRole() {
        return roleModelRepository.findByName(Roles.PROJECT_MEMBER)
                .orElseThrow(() -> new RuntimeException("Роль не найдена в БД"));
    }

    private void validateRegistrationData(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
    }
}