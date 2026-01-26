package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.request.PatchUserRequest;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;

public interface UserService {
    UserInformation updateUserInfo(User user, String email, PatchUserRequest req);
}
