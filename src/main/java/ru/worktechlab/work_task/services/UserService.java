package ru.worktechlab.work_task.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.Users;
import ru.worktechlab.work_task.models.mappers.UserMapper;
import ru.worktechlab.work_task.models.request_dto.RegisterDTO;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;
import ru.worktechlab.work_task.exceptions.InvalidUserException;
import ru.worktechlab.work_task.repositories.UserRepository;

import java.util.Collection;
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
    public void registerUser(RegisterDTO registerDto) {
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

    public List<Users> findAllByIds(Collection<String> ids) {
        return userRepository.findAllById(ids);
    }
}