package ru.worktechlab.work_task.user;

import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.mappers.UserMapper;
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
import ru.worktechlab.work_task.user.service.RoleService;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class AccountController {

    private final RoleService roleService;

    private final UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/registry")
    public ResponseEntity<?> registry(@Valid @RequestBody RegisterDto registerDto) {
            RoleModel defaultRole = roleService.getDefaultRole();
            Users user = userMapper.registerDtoToUser(registerDto, defaultRole);
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            userRepository.save(user);
            return new ResponseEntity<>("Пользователь успешно зарегестрирован", HttpStatus.OK);
    }
}