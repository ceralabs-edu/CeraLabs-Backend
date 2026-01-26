package app.demo.neurade.domain.models;

import java.util.Map;

public class RoleType {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String STUDENT = "ROLE_STUDENT";
    public static final String TEACHER = "ROLE_TEACHER";
    public static final String ORGANIZATION = "ROLE_ORGANIZATION";

    private static final Map<String, Short> ROLE_MAP = Map.of(
        ADMIN, (short) 1,
        ORGANIZATION, (short) 2,
        TEACHER, (short) 3,
        STUDENT, (short) 4
    );

    public static Short getRoleId(String roleName) {
        return ROLE_MAP.get(roleName);
    }
}
