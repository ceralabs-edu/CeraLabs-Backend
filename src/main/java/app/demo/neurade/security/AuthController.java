package app.demo.neurade.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    @Operation(summary = "Register user", description = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        UserDetails userDetails = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                userDetails = (UserDetails) principal;
            }
        }
        return ResponseEntity.ok(authService.register(userDetails, request));
    }

    @Operation(summary = "Login user", description = "Authenticate user and redirect to frontend")
    @PostMapping("/login")
    public void login(@RequestBody AuthRequest request, HttpServletResponse response) throws IOException {
        List<String> tokens = authService.login(request);
        cookieService.setTokenCookies(response, tokens.get(0), tokens.get(1));
        response.sendRedirect(frontendUrl + "/");
    }
}
