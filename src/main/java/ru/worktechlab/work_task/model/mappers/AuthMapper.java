package ru.worktechlab.work_task.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.model.rest.LoginRequest;
import ru.worktechlab.work_task.model.rest.LoginResponse;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "principal", source = "username")
    @Mapping(target = "credentials", source = "password")
    default UsernamePasswordAuthenticationToken toAuthenticationToken(LoginRequest loginRequest) {
        return new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @Mapping(target = "username", source = "userDetails.fullName")
    @Mapping(target = "jwtToken", source = "jwtToken")
    LoginResponse toLoginResponse(CustomUserDetails userDetails, String jwtToken);
}
