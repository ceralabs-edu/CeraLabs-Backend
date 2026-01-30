package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.dtos.requests.ClassCreationRequest;
import app.demo.neurade.domain.dtos.requests.UserInstanceUsageCreationRequest;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;

import java.util.List;
import java.util.UUID;

public interface ClassService {
    Classroom createClass(User creator, ClassCreationRequest req);
    List<Classroom> getAllClassesUnderManagement(User manager);
    Classroom getClass(Long classId);
    AssignmentDTO getAssignment(UUID assignmentId);
    List<UserDTO> getParticipantsInClass(Long classId);
    void addParticipants(Long classId, List<Long> userIds);
    void setClassInstanceUsageLimit(Long classId, UserInstanceUsageCreationRequest req);
    List<Classroom> getAllClasses();
}
