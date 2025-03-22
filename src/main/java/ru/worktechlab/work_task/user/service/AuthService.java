package ru.worktechlab.work_task.user.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.jwt.JwtUtils;
import ru.worktechlab.work_task.model.mappers.AuthMapper;
import ru.worktechlab.work_task.model.rest.LoginRequest;
import ru.worktechlab.work_task.model.rest.LoginResponse;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthMapper authMapper;

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, AuthMapper authMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.authMapper = authMapper;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                authMapper.toAuthenticationToken(loginRequest));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);
        return authMapper.toLoginResponse(userDetails, jwtToken);
    }

}
