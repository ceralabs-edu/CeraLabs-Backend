package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.requests.ChangeUserPasswordRequest;
import app.demo.neurade.domain.dtos.requests.ChangeUserRoleRequest;
import app.demo.neurade.services.AdminService;
import app.demo.neurade.services.JwtAccessTokenService;
import app.demo.neurade.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final JwtAccessTokenService jwtAccessTokenService;

    @Operation(summary = "Update user role", description = "Change the role of a user")
    @PatchMapping("/users/{email}/role")
    public ResponseEntity<?> updateRole(@RequestBody ChangeUserRoleRequest req) {
        adminService.changeRole(req.getEmail(), req.getRoleId());
        return ResponseEntity.ok(
                Map.of(
                        "message", "User role updated successfully"
                )
        );
    }

    @Operation(summary = "Change user password", description = "Change the password of a user")
    @PatchMapping("/users/{email}/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangeUserPasswordRequest req) {
        adminService.changePassword(req.getEmail(), req.getNewPassword());
        return ResponseEntity.ok(
                Map.of(
                        "message", "User password changed successfully"
                )
        );
    }

    @GetMapping("/statistic/all-users")
    public ResponseEntity<?> getAllUsersStatistic() {
        var stats = userService.getAllUsersAndInfo();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/jwt-token/{token}")
    public ResponseEntity<?> deleteJwtToken(@PathVariable String token) {
        jwtAccessTokenService.revokeTokenByValue(token);
        return ResponseEntity.ok(
            Map.of(
                "message", "JWT access token revoked successfully"
            )
        );
    }
}
