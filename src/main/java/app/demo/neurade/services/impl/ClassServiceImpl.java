package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.AssignmentDTO;
import app.demo.neurade.domain.dtos.UserDTO;
import app.demo.neurade.domain.dtos.requests.ClassCreationRequest;
import app.demo.neurade.domain.dtos.requests.TokenUsageLimit;
import app.demo.neurade.domain.dtos.requests.UserInstanceUsageCreationRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.*;
import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.repositories.*;
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
    private final UserRepository userRepository;
    private final UserInstanceUsageRepository userInstanceUsageRepository;
    private final AIPackageInstanceRepository aiPackageInstanceRepository;

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

    @Override
    public List<UserDTO> getParticipantsInClass(Long classId) {
        List<ClassParticipant> participants = participantRepository.findAllByClazz_Id(classId);
        return participants.stream()
                .map(ClassParticipant::getUser)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void addParticipants(Long classId, List<Long> userIds) {
        for (Long userId : userIds) {
            if (!participantRepository.existsByClazz_IdAndUser_Id(classId, userId)) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

                Classroom classroom = classRepository.findById(classId)
                        .orElseThrow(() -> new IllegalArgumentException("Class with ID " + classId + " not found"));

                ClassParticipant participant = ClassParticipant.builder()
                        .user(user)
                        .clazz(classroom)
                        .joinedAt(LocalDateTime.now())
                        .build();

                participantRepository.save(participant);
            }
        }
    }

    @Override
    @Transactional
    public void setClassInstanceUsageLimit(Long classId, UserInstanceUsageCreationRequest req) {
        Long userId = req.getUserId();
        TokenUsageLimit limit = req.getUsageLimit();
        UUID instanceId = req.getInstanceId();

        if (!participantRepository.existsByClazz_IdAndUser_Id(classId, userId)) {
            throw new RuntimeException("User is not a participant of the class");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        AIPackageInstance instance = aiPackageInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("AI Package Instance not found with id: " + instanceId));

        userInstanceUsageRepository.findForUpdate(user, instanceId)
                .ifPresentOrElse(

                        // Update existing usage record
                        usage -> {
                            usage.setLimitToken(limit.getTokenLimit());
                            usage.setRateLimitTokenLimit(limit.getRateLimitTokenMax());
                            usage.setRateLimitDurationDays(limit.getDurationInDays());
                            userInstanceUsageRepository.save(usage);
                        },


                        // Create new usage record if not exists
                        () -> {
                            var usage = UserAIInstanceUsage.builder()
                                    .user(user)
                                    .instance(instance)
                                    .limitToken(limit.getTokenLimit())
                                    .rateLimitTokenLimit(limit.getRateLimitTokenMax())
                                    .rateLimitDurationDays(limit.getDurationInDays())
                                    .tokenUsed(0L)
                                    .build();
                            userInstanceUsageRepository.save(usage);
                        }
                );
    }

    @Override
    public List<Classroom> getAllClasses() {
        return classRepository.findAll();
    }
}
