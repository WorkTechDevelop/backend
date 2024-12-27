package com.example.work_task.user;

import com.example.work_task.model.db.RoleModel;
import com.example.work_task.model.db.Users;
import com.example.work_task.model.db.enums.Roles;
import com.example.work_task.model.rest.RegisterDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work-task/v1")
@AllArgsConstructor
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleModelRepository roleModelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/registry")
    public  ResponseEntity<?> registry(@RequestBody RegisterDto registerDto) {

        if(userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Пользователь с таким email уже зарегестрирован", HttpStatus.BAD_REQUEST);
        }
        if(!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            return new ResponseEntity<>("Пароли не совпадают", HttpStatus.BAD_REQUEST);
        }

        Users user = new Users();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setMiddleName(registerDto.getMiddleName());
            user.setPhone(registerDto.getPhone());
            user.setGender(registerDto.getGender());
            user.setActive(false);

            RoleModel defaultRole = roleModelRepository.findByName(Roles.PROJECT_MEMBER)
                    .orElseThrow(() -> new RuntimeException("Роль не найдена в БД"));
            user.setRole(defaultRole);
            userRepository.save(user);

            return new ResponseEntity<>("Пользователь успешно зарегестрирован", HttpStatus.OK);
    }
}
