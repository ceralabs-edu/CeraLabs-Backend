package app.demo.neurade.security;

import app.demo.neurade.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    private final CookieService cookieService;
    @Value("${application.frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        if (email == null || email.isBlank()) {
            log.error("Email not found in OAuth2 attributes");
            response.sendRedirect(frontendUrl + "/?error=email_not_found");
            return;
        }

        findOrCreateUser(email, oauth2User);

        List<String> tokens = generateTokens(email);
        cookieService.setTokenCookies(response, tokens.get(0), tokens.get(1));

        response.sendRedirect(frontendUrl + "/");
    }

    private void findOrCreateUser(String email, OAuth2User oauth2User) {
        userService.createUserFromOAuth(email, oauth2User);
    }

    private List<String> generateTokens(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.refreshToken(userDetails);
        return List.of(accessToken, refreshToken);
    }
}
