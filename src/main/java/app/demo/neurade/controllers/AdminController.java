package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.request.ChangeUserPasswordRequest;
import app.demo.neurade.domain.dtos.request.ChangeUserRoleRequest;
import app.demo.neurade.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/users/{email}/role")
    public ResponseEntity<?> updateRole(@RequestBody ChangeUserRoleRequest req) {
        adminService.changeRole(req.getEmail(), req.getRole());
        return ResponseEntity.ok(
                Map.of(
                        "message", "User role updated successfully"
                )
        );
    }

    @PatchMapping("/users/{email}/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangeUserPasswordRequest req) {
        adminService.changePassword(req.getEmail(), req.getNewPassword());
        return ResponseEntity.ok(
                Map.of(
                        "message", "User password changed successfully"
                )
        );
    }
}
