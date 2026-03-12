package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.JwtAccessToken;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.infrastructures.repositories.JwtAccessTokenRepository;
import app.demo.neurade.services.JwtAccessTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtAccessTokenServiceImpl implements JwtAccessTokenService {
    private final JwtAccessTokenRepository jwtAccessTokenRepository;

    @Override
    @Transactional
    public JwtAccessToken revokeTokenByValue(String tokenValue) {
        JwtAccessToken token = jwtAccessTokenRepository.findByToken(tokenValue).orElseThrow();
        if (token.getStatus() != JwtAccessToken.Status.ACTIVE) {
            return token; // Already inactive, no need to update
        }
        token.setStatus(JwtAccessToken.Status.INACTIVE);
        token.setRevokedAt(LocalDateTime.now());
        return jwtAccessTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void deactivateAllActiveTokensForUser(User user) {
        var tokens = jwtAccessTokenRepository.findAllByUser(user);
        var now = LocalDateTime.now();
        var updated = tokens.stream()
            .filter(token -> token.getStatus() == JwtAccessToken.Status.ACTIVE)
            .peek(token -> {
                token.setStatus(JwtAccessToken.Status.INACTIVE);
                token.setRevokedAt(now);
            })
            .toList();
        if (!updated.isEmpty()) {
            jwtAccessTokenRepository.saveAll(tokens);
        }
    }

    @Override
    @Transactional
    public void saveNewTokenForUser(User user, String token, boolean disableOldTokenOnLogin, long expiresInSeconds) {
        if (disableOldTokenOnLogin) {
            deactivateAllActiveTokensForUser(user);
        }
        JwtAccessToken newToken = JwtAccessToken.builder()
                .user(user)
                .token(token)
                .status(JwtAccessToken.Status.ACTIVE)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds))
                .build();
        jwtAccessTokenRepository.save(newToken);
    }
}
