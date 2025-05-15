package ru.worktechlab.work_task.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.authorization.jwt.JwtUtils;
import ru.worktechlab.work_task.config.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtils jwtUtils;

    public String generateToken(CustomUserDetails userDetails) {
        return jwtUtils.generateTokenFromUserDetails(userDetails);
    }

    public String getUserGuidFromJwtToken(String jwtToken) {
        return jwtUtils.getUserGuidFromJwtToken(jwtToken);
    }
}
