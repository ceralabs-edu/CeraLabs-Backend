package app.demo.neurade.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AssignmentJudgeService {
    Map<String, String> checkAnswers(Map<String, MultipartFile> answers);
}
