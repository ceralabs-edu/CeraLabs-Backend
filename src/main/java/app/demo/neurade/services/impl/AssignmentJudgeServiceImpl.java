package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.StudentAnswer;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.infrastructures.chatbot_llm.ChatbotClient;
import app.demo.neurade.infrastructures.chatbot_llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import app.demo.neurade.infrastructures.repositories.AssignmentQuestionRepository;
import app.demo.neurade.infrastructures.repositories.StudentAnswerRepository;
import app.demo.neurade.services.AssignmentJudgeService;
import app.demo.neurade.services.FileService;
import app.demo.neurade.services.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentJudgeServiceImpl implements AssignmentJudgeService {

    private final ChatbotClient chatbotClient;
    private final ImageService imageService;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final FileService fileService;
    private final StudentAnswerRepository studentAnswerRepository;
    @Value("${llm.api-key}")
    private String apiKey;

    @Override
    @Transactional
    public Map<String, String> checkAnswers(User user, Map<String, MultipartFile> answers) {
        // Create a thread pool with 10 threads for parallel processing
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Map<String, String> result = new ConcurrentHashMap<>();

        List<UUID> questionIds = answers.keySet().stream()
                .map(UUID::fromString)
                .toList();

        Map<UUID, AssignmentQuestion> questionMap = assignmentQuestionRepository
                .findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(AssignmentQuestion::getId, q -> q));

        try {
            // Create a list of futures for all answer checking tasks
            List<CompletableFuture<Void>> futures = answers.entrySet().stream()
                    .map(entry -> CompletableFuture.runAsync(() -> {
                        try {
                            UUID uuid = UUID.fromString(entry.getKey());
                            MultipartFile file = entry.getValue();
                            AssignmentQuestion question = questionMap.get(uuid);

                            String judgement = null;
                            if (!(file == null || file.isEmpty())) {
                                judgement = checkAnswer(question, file);
                            }

                            // Check if a record already exists for this student-question pair
                            Optional<StudentAnswer> existingAnswer = studentAnswerRepository
                                    .findByStudent_IdAndQuestion_Id(user.getId(), uuid);

                            StudentAnswer studentAnswer;
                            if (existingAnswer.isPresent()) {
                                // Update existing record
                                studentAnswer = existingAnswer.get();
                                studentAnswer.setJudgement(judgement);
                                log.info("Updating existing answer for student {} and question {}", user.getId(), uuid);
                            } else {
                                // Create new record
                                studentAnswer = StudentAnswer.builder()
                                        .student(user)
                                        .question(question)
                                        .judgement(judgement)
                                        .build();
                                log.info("Creating new answer for student {} and question {}", user.getId(), uuid);
                            }
                            studentAnswerRepository.save(studentAnswer);

                            result.put(entry.getKey(), judgement);
                        } catch (Exception e) {
                            log.error("Error processing answer for question: {}", entry.getKey(), e);
                            throw new RuntimeException("Failed to process answer for question: " + entry.getKey(), e);
                        }
                    }, executorService))
                    .toList();

            // Wait for all tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        } finally {
            // Shutdown the executor service
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        return result;
    }

    private String checkAnswer(AssignmentQuestion question, MultipartFile answerImage) {
        log.info("Preparing to judge answer for question ID: {}", question.getId());
        log.info("Composing question image for question ID: {}", question.getId());
        BufferedImage image = imageService.concatQuestionImages(question);
        log.info("Compose done. Uploading assets for question ID: {}", question.getId());
        List<String> assetUrls = new ArrayList<>();
        String imageUrl = fileService.uploadAssignmentConcatedImage(question.getId(), image);
        assetUrls.add(imageUrl);
        assetUrls.addAll(fileService.uploadAssignmentAnswers(question.getId(), List.of(answerImage)));

        log.info("Calling workflow to judge answer for question ID: {}", question.getId());
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
        log.info("Judgement for question ID {}: {}", question.getId(), judgement);
        return judgement;
    }

    @Override
    public Map<UUID, String> getJudgementResults(User user, UUID assignmentId) {
        List<StudentAnswer> answers = studentAnswerRepository.findAllByStudent_IdAndQuestion_Assignment_Id(
                user.getId(),
                assignmentId
        );
        Map<UUID, String> result = new HashMap<>();
        for (StudentAnswer answer : answers) {
            result.put(answer.getQuestion().getId(), answer.getJudgement());
        }

        List<AssignmentQuestion> allQuestions = assignmentQuestionRepository.findAllByAssignment_Id(assignmentId);
        for (AssignmentQuestion question : allQuestions) {
            result.putIfAbsent(question.getId(), null);
        }
        return result;
    }
}
