package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.dtos.requests.PatchUserRequest;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;

import java.util.List;

public interface UserService {
    UserInformation updateUserInfo(User user, String email, PatchUserRequest req);
    List<UserDTO> getManagedUsers(User user);
}
