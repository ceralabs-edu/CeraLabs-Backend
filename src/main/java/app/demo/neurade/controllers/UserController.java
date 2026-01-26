package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.request.PatchUserRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final Mapper mapper;

    @Operation(summary = "Update user information", description = "Update user profile information")
    @PatchMapping("/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody PatchUserRequest req) {
        UserInformation info = userService.updateUserInfo(email, req);
        return ResponseEntity.ok(
                Map.of(
                        "message", "User information updated successfully",
                        "data", mapper.toDto(info)
                )
        );
    }
}
