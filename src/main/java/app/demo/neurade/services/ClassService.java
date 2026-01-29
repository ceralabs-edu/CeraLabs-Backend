package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.requests.ClassCreationRequest;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;

import java.util.List;

public interface ClassService {
    Classroom createClass(User creator, ClassCreationRequest req);
    List<Classroom> getAllClassesUnderManagement(User manager);
    Classroom getClass(Long classId);
}
