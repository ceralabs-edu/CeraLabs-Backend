package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.chatbot_llm.ChatbotClient;
import app.demo.neurade.infrastructures.chatbot_llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import app.demo.neurade.infrastructures.repositories.AssignmentQuestionRepository;
import app.demo.neurade.services.AssignmentJudgeService;
import app.demo.neurade.services.FileService;
import app.demo.neurade.services.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentJudgeServiceImpl implements AssignmentJudgeService {

    private final ChatbotClient chatbotClient;
    private final ImageService imageService;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final FileService fileService;
    @Value("${llm.api-key}")
    private String apiKey;

    @Override
    public Map<String, String> checkAnswers(Map<String, MultipartFile> answers) {
        Map<String, String> result = new HashMap<>();
        for (var entry : answers.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            MultipartFile file = entry.getValue();
            result.put(uuid.toString(), checkAnswer(uuid, file));
        }
        return result;
    }

    private String checkAnswer(UUID questionId, MultipartFile answerImage) {
        AssignmentQuestion question = assignmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + questionId));
        log.info("Preparing to judge answer for question ID: {}", questionId);
        log.info("Composing question image for question ID: {}", questionId);
        BufferedImage image = imageService.concatQuestionImages(question);
        log.info("Compose done. Uploading assets for question ID: {}", questionId);
        List<String> assetUrls = new ArrayList<>();
        String imageUrl = fileService.uploadAssignmentConcatedImage(questionId, image);
        assetUrls.add(imageUrl);
        assetUrls.addAll(fileService.uploadAssignmentAnswers(questionId, List.of(answerImage)));

        log.info("Calling workflow to judge answer for question ID: {}", questionId);
        log.info("Asset URLs: {}", assetUrls);

        WorkflowResponse response = chatbotClient.callWorkflow(
                List.of(
                        new WorkflowRequest.Query(
                                "user",
                                "Đánh giá bài làm cho câu hỏi sau, dựa trên các đáp án và lời giải được cung cấp."
                        )
                ),
                apiKey,
                assetUrls
        );
        String judgement = response.getGuardianRaw().getContent();
        log.info("Judgement for question ID {}: {}", questionId, judgement);
        return judgement;
    }
}
