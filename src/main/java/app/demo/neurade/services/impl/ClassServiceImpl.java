package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.requests.ClassCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.ClassParticipant;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.repositories.AssignmentQuestionRepository;
import app.demo.neurade.infrastructures.repositories.AssignmentRepository;
import app.demo.neurade.infrastructures.repositories.ClassRepository;
import app.demo.neurade.infrastructures.repositories.ParticipantRepository;
import app.demo.neurade.services.ClassService;
import app.demo.neurade.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final UserService userService;
    private final AssignmentRepository assignmentRepository;
    private final ParticipantRepository participantRepository;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final Mapper mapper;

    @Override
    @Transactional
    public Classroom createClass(User creator, ClassCreationRequest req) {
        Classroom clazz = Classroom.builder()
                .name(req.getClassName())
                .description(req.getDescription())
                .creator(creator)
                .build();

        Classroom classroom = classRepository.save(clazz);

        ClassParticipant creatorParticipant = ClassParticipant.builder()
                .user(creator)
                .clazz(clazz)
                .joinedAt(LocalDateTime.now())
                .build();

        participantRepository.save(creatorParticipant);

        return classroom;
    }

    @Override
    public List<Classroom> getAllClassesUnderManagement(User manager) {
        List<User> managedUsers = userService.getUsersUnderManagement(manager);
        return classRepository.findByCreatorIn(managedUsers);
    }

    @Cacheable(
            value = "classroom",
            key = "#classId",
            unless = "#result == null"
    )
    @Override
    public Classroom getClass(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class with ID " + classId + " not found"));
    }

    @Override
    @Cacheable(
            value = "assignment",
            key = "#assignmentId",
            unless = "#result == null"
    )
    public AssignmentDTO getAssignment(UUID assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment with ID " + assignmentId + " not found"));

        List<AssignmentQuestion> questions = assignmentQuestionRepository.findAllByAssignment(assignment);
        return mapper.toDto(assignment, questions, false);

    }
}
