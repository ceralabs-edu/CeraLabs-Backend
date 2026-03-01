package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.UserAndInfoDTO;
import app.demo.neurade.domain.dtos.requests.PatchUserRequest;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.security.RegisterRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface UserService {
    UserInformation updateUserInfo(User user, String email, PatchUserRequest req);
    List<User> getUsersUnderManagement(User user);
    UserAndInfoDTO getUserAndInfo(Long id);
    List<UserAndInfoDTO> getAllUsersAndInfo();

    // User creation methods
    User createUserWithInformation(RegisterRequest req);
    User createUserFromOAuth(String email, OAuth2User oauth2User);
}
