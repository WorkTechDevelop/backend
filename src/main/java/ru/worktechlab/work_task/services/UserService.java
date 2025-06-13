package ru.worktechlab.work_task.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.dto.request_dto.RegisterDTO;
import ru.worktechlab.work_task.exceptions.InvalidUserException;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.UserMapper;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.UserRepository;
import ru.worktechlab.work_task.repositories.UsersProjectsRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        User user = userMapper.registerDtoToUser(registerDto, defaultRole);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(user);
    }

    public List<String> getProjectUserIds(User user) {
        return usersProjectsRepository.findUserByProjectId(user.getLastProjectId());
    }

    public String extractUserId(String jwtToken) {
        return tokenService.getUserGuidFromJwtToken(jwtToken);
    }

    public User findUserById(String userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь с ID %s не найден или не активен", userId)));
    }

    public List<User> findAllByIds(Collection<String> ids) {
        return userRepository.findAllById(ids);
    }

    @TransactionMandatory
    public User findUserByEmail(String email) {
        return userRepository.findExistUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с email %s не найден или не активен" + email));
    }

    @TransactionMandatory
    public void checkHasProjectForUser(String userId,
                                       String projectId) throws NotFoundException {
        User user = findUserById(userId);
        boolean hasProject = user.getProjects().stream()
                .anyMatch(pr -> Objects.equals(projectId, pr.getId()));
        if (!hasProject)
            throw new NotFoundException(
                    String.format("Вам не доступен проект с ИД - %s", projectId)
            );
    }
}