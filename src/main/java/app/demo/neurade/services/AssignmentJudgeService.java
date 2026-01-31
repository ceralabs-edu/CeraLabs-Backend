package app.demo.neurade.services;

import app.demo.neurade.domain.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

public interface AssignmentJudgeService {
    Map<String, String> checkAnswers(User user, Map<String, MultipartFile> answers);
    Map<UUID, String> getJudgementResults(User user, UUID assignmentId);
}
