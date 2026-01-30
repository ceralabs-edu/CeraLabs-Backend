package app.demo.neurade.domain.models;

import lombok.Getter;

@Getter
public enum RoleType {

    ADMIN("ROLE_ADMIN", (short) 1),
    ORGANIZATION("ROLE_ORGANIZATION", (short) 2),
    TEACHER("ROLE_TEACHER", (short) 3),
    STUDENT("ROLE_STUDENT", (short) 4);

    private final String roleName;
    private final short roleId;

    RoleType(String roleName, short roleId) {
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public static RoleType getRoleById(short id) {
        for (RoleType role : RoleType.values()) {
            if (role.getRoleId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role found with id " + id);
    }

}

