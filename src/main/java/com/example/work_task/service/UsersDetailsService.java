package com.example.work_task.service;

import com.example.work_task.config.CustomUserDetails;
import com.example.work_task.model.RoleModel;
import com.example.work_task.model.Users;
import com.example.work_task.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsersDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       ;
        return userRepository.findByEmail(email).map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(email + " Пользователь не найден"));
    }

    @Transactional(readOnly = true)
    public RoleModel getUserRoleByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findRoleByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " Роль пользователя не найдена"));
    }

}
