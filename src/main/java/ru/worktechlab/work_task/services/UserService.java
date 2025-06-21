package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionMandatory;
import ru.worktechlab.work_task.annotations.TransactionRequired;
import ru.worktechlab.work_task.config.params.MailParams;
import ru.worktechlab.work_task.dto.OkResponse;
import ru.worktechlab.work_task.dto.StringIdsDto;
import ru.worktechlab.work_task.dto.users.RegisterDTO;
import ru.worktechlab.work_task.dto.users.UserShortDataDto;
import ru.worktechlab.work_task.exceptions.NotFoundException;
import ru.worktechlab.work_task.mappers.UserMapper;
import ru.worktechlab.work_task.models.tables.RoleModel;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final MailParams mailParams;

    @TransactionRequired
    public void registerUser(RegisterDTO registerDto) {
        RoleModel defaultRole = roleService.getDefaultRole();
        User user = userMapper.registerDtoToUser(registerDto, defaultRole);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        if (mailParams.isEnable()) {
            user.setConfirmationToken(UUID.randomUUID().toString());
            userRepository.save(user);
            notificationService.sendConfirmationToken(user);
        } else {
            user.setConfirmedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    @TransactionMandatory
    public User findActiveUserById(String userId) {
        return userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь с ID %s не найден или не активен", userId)));
    }

    @TransactionMandatory
    public User findActiveUserByEmail(String email) {
        return userRepository.findActiveUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с email %s не найден или не активен" + email));
    }

    @TransactionMandatory
    public void checkHasProjectForUser(User user,
                                       String projectId) throws NotFoundException {
        boolean hasProject = user.getProjects().stream()
                .anyMatch(pr -> Objects.equals(projectId, pr.getId()));
        if (!hasProject)
            throw new NotFoundException(
                    String.format("Вам не доступен проект с ИД - %s", projectId)
            );
    }

    @TransactionRequired
    public boolean emailConfirmation(String token) {
        User user = findUserByConfirmationToken(token);
        user.setConfirmationToken(null);
        user.setConfirmedAt(LocalDateTime.now());
        return true;
    }

    @TransactionMandatory
    public User findUserByConfirmationToken(String token) {
        return userRepository.findExistUserByConfirmationToken(token)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь с токеном подтверждения %s не найден" + token));
    }

    @TransactionRequired
    public List<UserShortDataDto> getAllUsers() {
        List<User> users = getUsers();
        if (CollectionUtils.isEmpty(users))
            return Collections.emptyList();
        return userMapper.toShortDataList(users);
    }

    @TransactionMandatory
    public List<User> getUsers() {
        return userRepository.getUsers().stream()
                .sorted(Comparator.comparing(User::getLastName)
                        .thenComparing(User::getFirstName))
                .toList();
    }

    @TransactionRequired
    public List<UserShortDataDto> findUsersByIdsIn(StringIdsDto data) throws NotFoundException {
        if (data == null || CollectionUtils.isEmpty(data.getIds()))
            return Collections.emptyList();
        List<User> users = findAndCheckUsers(data.getIds());
        if (CollectionUtils.isEmpty(users))
            return Collections.emptyList();
        return userMapper.toShortDataList(users);
    }

    @TransactionMandatory
    List<User> findAndCheckUsers(List<String> userIds) throws NotFoundException {
        if (CollectionUtils.isEmpty(userIds))
            return Collections.emptyList();
        Map<String, User> userById = userRepository.findUsersByIdsIn(userIds)
                .collect(Collectors.toMap(User::getId, Function.identity()));
        for (String userId : userIds) {
            if (!userById.containsKey(userId))
                throw new NotFoundException(String.format("Пользователь с ID %s не найден или не активен", userId));
        }
        return userById.values().stream()
                .sorted(Comparator.comparing(User::getLastName)
                        .thenComparing(User::getFirstName))
                .toList();
    }

    @TransactionRequired
    public OkResponse activateUsers(StringIdsDto data) throws NotFoundException {
        OkResponse response = new OkResponse();
        if (data == null || CollectionUtils.isEmpty(data.getIds()))
            return response;
        List<User> users = findAndCheckUsers(data.getIds());
        users.forEach(user -> user.setActive(true));
        userRepository.flush();
        return response;
    }
}