package app.demo.neurade.domain.models;

import java.util.Map;

public class RoleType {
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    public static final Map<String, Short> ROLE_MAP = Map.of(
        USER, (short)1,
        ADMIN, (short)2
    );
}
