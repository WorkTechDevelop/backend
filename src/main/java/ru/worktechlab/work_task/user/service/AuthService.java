package ru.worktechlab.work_task.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.jwt.TokenService;
import ru.worktechlab.work_task.model.mappers.AuthMapper;
import ru.worktechlab.work_task.model.rest.LoginRequest;
import ru.worktechlab.work_task.model.rest.LoginResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                authMapper.toAuthenticationToken(loginRequest));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = tokenService.generateToken(userDetails);
        return authMapper.toLoginResponse(userDetails, jwtToken);
    }
}
