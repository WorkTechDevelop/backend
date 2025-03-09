package ru.worktechlab.work_task.model.db;

import lombok.Data;
import ru.worktechlab.work_task.model.db.enums.Gender;

@Data
public class UserBuilder {

    private String lastName;

    private String firstName;

    private String middleName;

    private String email;

    private String password;

    private String phone;

    private String role_id;

    private String birthDate;

    private boolean active;

    private Gender gender;

    private String lastProjectId;

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder middleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public UserBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserBuilder roleId(String roleId) {
        this.role_id = roleId;
        return this;
    }

    public UserBuilder birthDate(String birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public UserBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public UserBuilder gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserBuilder lastProjectId(String lastProjectId) {
        this.lastProjectId = lastProjectId;
        return this;
    }

    public Users build() {
        return Users.builder()
                .email(this.email)
                .password(this.password)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .middleName(this.middleName)
                .phone(this.phone)
                .gender(this.gender)
                .active(this.active)
                .role_id(this.role_id)
                .lastProjectId(this.lastProjectId)
                .build();
    }
}
