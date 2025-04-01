package ru.worktechlab.work_task.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.jwt.TokenService;
import ru.worktechlab.work_task.model.db.RoleModel;
import ru.worktechlab.work_task.model.db.Users;
import ru.worktechlab.work_task.model.mappers.UserMapper;
import ru.worktechlab.work_task.model.rest.RegisterDto;
import ru.worktechlab.work_task.projects.UsersProjectsRepository;
import ru.worktechlab.work_task.user.InvalidUserException;
import ru.worktechlab.work_task.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersProjectsRepository usersProjectsRepository;
    private final TokenService tokenService;

    @Transactional
    public void registerUser(RegisterDto registerDto) {
        RoleModel defaultRole = roleService.getDefaultRole();
        Users user = userMapper.registerDtoToUser(registerDto, defaultRole);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(user);
    }

    public List<String> getProjectUserIds(Users user) {
        return usersProjectsRepository.findUserByProjectId(user.getLastProjectId());
    }

    public Users getUser(String jwtToken) {
        String userId = extractUserId(jwtToken);
        return findUserById(userId);
    }

    public String extractUserId(String jwtToken) {
        return tokenService.getUserGuidFromJwtToken(jwtToken);
    }

    public Users findUserById(String userId) {
        return userRepository.findById(userId)
                .filter(Users::isActive)
                .orElseThrow(() -> new InvalidUserException(
                        String.format("Пользователь с ID %s не найден или не активен", userId)));
    }
}