package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.AssignmentQuestionDTO;
import app.demo.neurade.domain.dtos.requests.AssignmentCreationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    AssignmentDTO createAssignment(Long classId, AssignmentCreationRequest req);
    List<AssignmentQuestionDTO> createAndProcessPDF(UUID assignmentId, List<MultipartFile> files);
}
