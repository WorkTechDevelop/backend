package com.example.work_task.model;

public enum Roles {
    ADMIN("admin"),
    PROJECT_OWNER("project owner"),
    PROJECT_MEMBER("project member");

   private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
