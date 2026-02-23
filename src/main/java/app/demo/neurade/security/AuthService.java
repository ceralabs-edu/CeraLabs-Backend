package app.demo.neurade.security;

import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.*;
import app.demo.neurade.infrastructures.repositories.*;
import app.demo.neurade.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final Mapper mapper;


    @Transactional
    public RegisterResponse register(UserDetails userDetails, RegisterRequest req) {
        if (
                req.getRoleId() == RoleType.ADMIN.getRoleId() &&
                !(
                        userDetails instanceof CustomUserDetails cud
                        && cud.getUser().getRole().isRoleType(RoleType.ADMIN)
                )
        ) {
            log.warn("Only admin can register another admin");
            throw new IllegalArgumentException("Only admin can register another admin");
        }

        User user = userService.createUserWithInformation(req);

        return RegisterResponse.builder()
                .user(mapper.toDto(user))
                .message("User registered successfully")
                .build();
    }

    public List<String> login(AuthRequest req) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                req.getEmail(),
                req.getPassword()
        );
        auth = authManager.authenticate(auth);
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if (userDetails == null) {
            log.warn("Authentication failed for " + req.getEmail());
            throw new RuntimeException("Authentication failed");
        }

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.refreshToken(userDetails);

        return List.of(accessToken, refreshToken);
    }
}
