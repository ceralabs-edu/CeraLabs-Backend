package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.dtos.requests.PatchUserRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final Mapper mapper;

    @Operation(summary = "Update user information", description = "Update user profile information")
    @PatchMapping("/profile")
    public ResponseEntity<?> updateUser(@RequestBody PatchUserRequest req) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        UserInformation info = userService.updateUserInfo(user, req);
        return ResponseEntity.ok(
                Map.of(
                        "message", "User information updated successfully",
                        "data", mapper.toDto(info)
                )
        );
    }

    @GetMapping("/managed")
    @Operation(summary = "Get users under management", description = "Retrieve a list of users under the management of the current user")
    public ResponseEntity<?> getUsersUnderManagement() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UserDTO> users = userService.getUsersUnderManagement(userDetails.getUser())
                .stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(
                Map.of(
                        "message", "Users retrieved successfully",
                        "data", users
                )
        );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(
                userService.getUserAndInfo(Long.parseLong(userId))
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(
                userService.getUserAndInfo(user.getId())
        );
    }
}
