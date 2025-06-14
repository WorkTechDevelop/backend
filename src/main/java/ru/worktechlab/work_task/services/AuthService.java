package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.annotations.TransactionRequired;
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
    private final TokenService tokenService;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;

    @TransactionRequired
    public LoginResponseDTO authenticate(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                authMapper.toAuthenticationToken(loginRequestDTO)
        );

        User user = userService.findActiveUserByEmail(loginRequestDTO.getUsername());
        String accessToken = tokenService.generateToken(user);
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
        String newAccessToken = tokenService.generateToken(user);

        return new LoginResponseDTO(newAccessToken, refreshToken.getRefreshToken());
    }
}
