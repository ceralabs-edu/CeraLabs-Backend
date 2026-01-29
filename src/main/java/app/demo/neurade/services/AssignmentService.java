package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.requests.AssignmentCreationRequest;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentService {
    AssignmentDTO createAssignment(Long classId, AssignmentCreationRequest req);
    AssignmentQuestion createAndProcessQuestion(Long assignmentId, List<MultipartFile> files);
}
