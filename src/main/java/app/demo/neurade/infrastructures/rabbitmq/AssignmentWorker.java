package app.demo.neurade.infrastructures.rabbitmq;

import app.demo.neurade.configs.RabbitMQConfig;
import app.demo.neurade.domain.models.assignment.AssignmentQuestion;
import app.demo.neurade.domain.rabbitmq.AssignmentJob;
import app.demo.neurade.domain.rabbitmq.JobStatus;
import app.demo.neurade.infrastructures.chatbot_llm.ChatbotClient;
import app.demo.neurade.infrastructures.chatbot_llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import app.demo.neurade.infrastructures.repositories.AssignmentQuestionRepository;
import app.demo.neurade.services.AssignmentJudgeService;
import app.demo.neurade.services.FileService;
import app.demo.neurade.services.ImageService;
import app.demo.neurade.services.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentWorker {

    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final JobStatusService jobStatusService;
    private final ImageService imageService;
    private final ChatbotClient chatbotClient;
    private final FileService fileService;
    private final AssignmentJudgeService assignmentJudgeService;

    @RabbitListener(queues = RabbitMQConfig.ASSIGNMENT_QUEUE)
    public void processAssignmentJob(AssignmentJob job) {
        try {
            job.setStatus(JobStatus.PROCESSING);
            jobStatusService.saveJob(job);

            AssignmentQuestion question = assignmentQuestionRepository.findById(job.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "AssignmentQuestion not found with ID: " + job.getQuestionId()
                    ));

            String judgement = checkAnswer(
                    question,
                    job.getAnswerImageUrl(),
                    job.getApiKey()
            );

            assignmentJudgeService.saveJudgement(judgement, job.getUserId(), job.getQuestionId());

            job.setResponse(judgement);
            job.setStatus(JobStatus.COMPLETED);
            jobStatusService.saveJob(job);

            log.info("Completed AssignmentJob with ID: {}", job.getJobId());
        } catch (Exception e) {
            log.error("Error processing AssignmentJob with ID: {}", job.getJobId(), e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobStatusService.saveJob(job);
            throw e;
        }
    }

    private String checkAnswer(AssignmentQuestion question, String answerImageUrl, String apiKey) {
        log.info("Preparing to judge answer for question ID: {}", question.getId());
        log.info("Composing question image for question ID: {}", question.getId());
        BufferedImage image = imageService.concatQuestionImages(question);
        log.info("Compose done. Uploading assets for question ID: {}", question.getId());
        List<String> assetUrls = new ArrayList<>();
        String imageUrl = fileService.uploadAssignmentConcatedImage(question.getId(), image);
        assetUrls.add(imageUrl);
        assetUrls.add(answerImageUrl);

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
}
