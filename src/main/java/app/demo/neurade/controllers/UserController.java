package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.request.PatchUserRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody PatchUserRequest req) throws Exception {
        UserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null) throw new UnauthorizedException("Unauthorized");
        User currentUser = ((CustomUserDetails) userDetails).getUser();
        UserInformation info = userService.updateUserInfo(currentUser, email, req);
        return ResponseEntity.ok(
                Map.of(
                        "message", "User information updated successfully",
                        "data", mapper.toDto(info)
                )
        );
    }
}
