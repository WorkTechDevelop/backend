package ru.worktechlab.work_task.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.mappers.UserMapper;
import ru.worktechlab.work_task.model.rest.RegisterDto;
import ru.worktechlab.work_task.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterDto registerDto) {
        RoleModel defaultRole = roleService.getDefaultRole();
        Users user = userMapper.registerDtoToUser(registerDto, defaultRole);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(user);
    }
}
