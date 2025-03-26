package ru.worktechlab.work_task.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.worktechlab.work_task.config.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtils jwtUtils;

    public String generateToken(CustomUserDetails userDetails) {
        return jwtUtils.generateTokenFromUserDetails(userDetails);
    }
}
