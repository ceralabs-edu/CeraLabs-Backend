package app.demo.neurade.services;

import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.rabbitmq.AssignmentJob;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface AssignmentJudgeService {
    Map<String, String> checkAnswers(User user, Map<String, MultipartFile> answers);
    Map<UUID, String> getJudgementResults(User user, UUID assignmentId);
    void saveJudgement(String judgement, Long studentId, UUID questionId);
    AssignmentJob getAssignmentJob(UUID jobId);
}
