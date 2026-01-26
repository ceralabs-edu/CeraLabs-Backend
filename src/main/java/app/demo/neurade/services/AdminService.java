package app.demo.neurade.services;

import app.demo.neurade.domain.models.RoleType;
import app.demo.neurade.domain.models.User;

public interface AdminService {
    User changePassword(String email, String newPassword);
    User changeRole(String email, RoleType newRole);
}
