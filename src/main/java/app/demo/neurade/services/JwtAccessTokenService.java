package app.demo.neurade.services;

import app.demo.neurade.domain.models.User;

public interface JwtAccessTokenService {
    boolean revokeTokenByValue(String token);
    void deactivateAllActiveTokensForUser(User user);
    void saveNewTokenForUser(User user, String token, boolean disableOldTokenOnLogin, long expiresInSeconds);
}
