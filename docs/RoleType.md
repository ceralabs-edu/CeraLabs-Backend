# RoleType Documentation

## Overview
`RoleType` is an enum representing the different user roles in the system. Each role has a unique name and ID, and is used for authorization and access control throughout the application.

## Enum Definition
```
public enum RoleType {
    ADMIN("ROLE_ADMIN", (short) 1),
    ORGANIZATION("ROLE_ORGANIZATION", (short) 2),
    TEACHER("ROLE_TEACHER", (short) 3),
    STUDENT("ROLE_STUDENT", (short) 4);
    // ...
}
```

### Fields
- `roleName` (`String`): The name of the role, used in security contexts (e.g., `ROLE_ADMIN`).
- `roleId` (`short`): The unique numeric ID for the role.

### Methods
- `getRoleName()`: Returns the role's name.
- `getRoleId()`: Returns the role's ID.
- `getRoleById(short id)`: Returns the `RoleType` for the given ID, or throws an exception if not found.

## Usage
- **Security**: Role names are used in Spring Security annotations, e.g., `@PreAuthorize("hasRole('ADMIN')")` matches `ROLE_ADMIN`.
- **Constants**: Use the enum constants (e.g., `RoleType.ADMIN`) in your Java code for type safety.
- **Mapping**: Use `getRoleById` to map numeric IDs to roles.