package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.requests.AssignmentCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.repositories.AssignmentRepository;
import app.demo.neurade.infrastructures.repositories.ClassRepository;
import app.demo.neurade.services.AssignmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final ClassRepository classRepository;
    private final AssignmentRepository assignmentRepository;
    private final Mapper mapper;

    @Override
    @Transactional
    public AssignmentDTO createAssignment(Long classId, AssignmentCreationRequest req) {
        Classroom classroom = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Assignment assignment = Assignment.builder()
                .classroom(classroom)
                .title(req.getTitle())
                .description(req.getDescription())
                .deadline(req.getDeadline())
                .build();

        assignment = assignmentRepository.save(assignment);

        return mapper.toDto(assignment);
    }

    @Override
    public AssignmentQuestion createAndProcessQuestion(Long assignmentId, List<MultipartFile> files) {
        return null;
    }
}
