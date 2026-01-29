package app.demo.neurade.services;

import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;

import java.util.List;
import java.util.UUID;

public interface AssignmentQuestionPersistenceService {
    Assignment saveAssignmentWithQuestions(
            UUID assignmentId,
            List<AssignmentQuestion> questions
    );
}
