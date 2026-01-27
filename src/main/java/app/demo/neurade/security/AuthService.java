package app.demo.neurade.security;

import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.*;
import app.demo.neurade.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserInformationRepository userInformationRepository;
    private final RoleRepository roleRepository;
    private final ProvinceRepository provinceRepository;
    private final CommuneRepository communeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final Mapper mapper;


    @Transactional
    public RegisterResponse register(UserDetails userDetails, RegisterRequest req) {
        // Check if user already exists
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            log.warn(req.getEmail() + " is already registered");
            throw new IllegalArgumentException("User already exists with email: " + req.getEmail());
        }

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

        Role role = roleRepository.findById(req.getRoleId()).orElse(null);

        // Create new user
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)
                .build();

        user = userRepository.save(user);

        // Find province and commune
        Province city = provinceRepository.findByCode(req.getCityCode())
                .orElseThrow(() -> {
                    log.warn("City code " + req.getCityCode() + " cannot be found");
                    return new IllegalArgumentException("Province not found with id: " + req.getCityCode());
                });
        Commune commune = communeRepository.findByCode(req.getSubDistrictCode())
                .orElseThrow(() -> {
                    log.warn("Sub district code " + req.getSubDistrictCode() + " cannot be found");
                    return new IllegalArgumentException("Commune not found with id: " + req.getSubDistrictCode());
                });

        // Create new user information
        UserInformation userInfo = UserInformation.builder()
                .user(user)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .city(city)
                .subDistrict(commune)
                .bio(req.getBio())
                .school(req.getSchool())
                .grade(req.getGrade())
                .addressDetail(req.getAddressDetail())
                .dateOfBirth(req.getDateOfBirth())
                .favoriteSubjects(req.getFavoriteSubjects())
                .avatarImage(req.getAvatarUrl())
                .build();

        userInformationRepository.save(userInfo);

        return RegisterResponse.builder()
                .user(mapper.toDto(user))
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(AuthRequest req) {
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
        User user = ((CustomUserDetails) userDetails).getUser();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(mapper.toDto(user))
                .build();
    }
}
