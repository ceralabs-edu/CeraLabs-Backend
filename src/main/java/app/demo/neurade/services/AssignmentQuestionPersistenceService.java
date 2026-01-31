package app.demo.neurade.services;

import app.demo.neurade.domain.models.assignment.Assignment;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;

import java.util.List;

public interface AssignmentQuestionPersistenceService {
    Assignment saveAssignmentWithQuestions(
            Assignment assignment,
            List<AssignmentQuestion> questions
    );
}
