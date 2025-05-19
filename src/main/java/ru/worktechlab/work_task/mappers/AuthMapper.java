package ru.worktechlab.work_task.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import ru.worktechlab.work_task.config.CustomUserDetails;
import ru.worktechlab.work_task.dto.request_dto.LoginRequestDTO;
import ru.worktechlab.work_task.dto.response_dto.LoginResponseDTO;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    @Mapping(target = "principal", source = "username")
    @Mapping(target = "credentials", source = "password")
    default UsernamePasswordAuthenticationToken toAuthenticationToken(LoginRequestDTO loginRequestDTO) {
        return new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
    }

    @Mapping(target = "username", source = "userDetails.fullName")
    @Mapping(target = "jwtToken", source = "jwtToken")
    LoginResponseDTO toLoginResponse(CustomUserDetails userDetails, String jwtToken);
}
