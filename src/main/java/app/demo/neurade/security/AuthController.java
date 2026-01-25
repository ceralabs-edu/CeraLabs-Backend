package app.demo.neurade.security;

import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final Mapper mapper;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        RegisterResponse response = new RegisterResponse();
        response.setMessage("User registered successfully");
        response.setUser(mapper.toDto(user));
        return ResponseEntity.ok(response);
    }
}
