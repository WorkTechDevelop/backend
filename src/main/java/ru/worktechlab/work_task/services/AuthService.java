package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.dto.request_dto.TokenRefreshRequestDTO;
import ru.worktechlab.work_task.dto.response_dto.LoginResponseDTO;
import ru.worktechlab.work_task.exceptions.ExpiredTokenException;
import ru.worktechlab.work_task.exceptions.InvalidTokenException;
import ru.worktechlab.work_task.mappers.AuthMapper;
import ru.worktechlab.work_task.models.tables.RefreshToken;
import ru.worktechlab.work_task.models.tables.User;
import ru.worktechlab.work_task.repositories.RefreshTokenRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final TokenService tokenService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                authMapper.toAuthenticationToken(loginRequestDTO)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userService.findUserByEmail(userDetails.getUsername());

        String accessToken = tokenService.generateToken(userDetails);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken.getToken());
    }

    public LoginResponseDTO refreshAccessToken(TokenRefreshRequestDTO refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new ExpiredTokenException("Refresh token expired");
        }

        User user = token.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = tokenService.generateToken(userDetails);

        return new LoginResponseDTO(newAccessToken, refreshToken.getRefreshToken());
    }
}
