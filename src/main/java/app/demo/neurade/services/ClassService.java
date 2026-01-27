package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.requests.ClassCreationRequest;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;

public interface ClassService {
    Classroom createClass(User creator, ClassCreationRequest req);
}
