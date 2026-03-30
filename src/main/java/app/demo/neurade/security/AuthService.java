package app.demo.neurade.security;

import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.*;
import app.demo.neurade.infrastructures.repositories.*;
import app.demo.neurade.services.UserService;
import app.demo.neurade.services.JwtAccessTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserRepository userRepository;
    private final JwtAccessTokenService jwtAccessTokenService;
    @Value("${application.security.disable-old-token-on-oauth2-login:false}")
    private boolean disableOldTokenOnLogin;


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

        // Save access token to DB (simulate login after register)
        UserDetails newUserDetails = org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(user.getRole().getName())
            .build();
        String accessToken = jwtService.generateToken(newUserDetails);
        jwtAccessTokenService.saveNewTokenForUser(
            user,
            accessToken,
            false, // When register, we have no old token to disable, so set to false
            jwtService.getJwtExpirationSeconds()
        );

        return RegisterResponse.builder()
                .user(mapper.toDto(user))
                .message("User registered successfully")
                .build();
    }

    @Transactional
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

        var userOpt = userRepository.findByEmail(userDetails.getUsername());
        userOpt.ifPresent(user -> jwtAccessTokenService.saveNewTokenForUser(
            user,
            accessToken,
            disableOldTokenOnLogin,
            jwtService.getJwtExpirationSeconds()
        ));

        return List.of(accessToken, refreshToken);
    }
}
