package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.mappers.AuthMapper;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.dto.response_dto.LoginResponseDTO;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final TokenService tokenService;

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                authMapper.toAuthenticationToken(loginRequestDTO));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = tokenService.generateToken(userDetails);
        return authMapper.toLoginResponse(userDetails, jwtToken);
    }
}
