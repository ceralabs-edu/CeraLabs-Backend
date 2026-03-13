package app.demo.neurade.services;

import app.demo.neurade.domain.models.User;

public interface UserPersistenceService {
    User createUserAndInfo(UserAndInfoParams params);
}
