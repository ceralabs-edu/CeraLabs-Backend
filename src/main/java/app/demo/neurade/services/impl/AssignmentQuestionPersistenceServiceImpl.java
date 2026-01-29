package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.repositories.AssignmentQuestionRepository;
import app.demo.neurade.services.AssignmentQuestionPersistenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentQuestionPersistenceServiceImpl implements AssignmentQuestionPersistenceService {

    private final AssignmentQuestionRepository assignmentQuestionRepository;

    @Override
    @Transactional
    public Assignment saveAssignmentWithQuestions(Assignment assignment, List<AssignmentQuestion> questions) {
        questions.forEach(q -> q.setAssignment(assignment));
        assignmentQuestionRepository.saveAll(questions);
        log.info("Saved {} questions for assignment {}", questions.size(), assignment.getTitle());
        return assignment;
    }
}
