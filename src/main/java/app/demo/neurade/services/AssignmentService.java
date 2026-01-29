package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.requests.AssignmentCreationRequest;

public interface AssignmentService {
    AssignmentDTO createAssignment(Long classId, AssignmentCreationRequest req);
}
