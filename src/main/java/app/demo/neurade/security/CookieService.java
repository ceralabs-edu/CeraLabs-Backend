package app.demo.neurade.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CookieService {
    @Value("${application.security.jwt.expiration}")
    private Integer jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private Integer refreshTokenExpiration;

    public void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(jwtExpiration / 1000);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(refreshTokenExpiration / 1000);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
