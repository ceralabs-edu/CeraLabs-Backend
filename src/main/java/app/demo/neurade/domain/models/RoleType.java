package app.demo.neurade.domain.models;

import lombok.Getter;

@Getter
public enum RoleType {

    ADMIN("ROLE_ADMIN"),
    ORGANIZATION("ROLE_ORGANIZATION"),
    TEACHER("ROLE_TEACHER"),
    STUDENT("ROLE_STUDENT");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

}

