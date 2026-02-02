package app.demo.neurade.infrastructures.rabbitmq;

import app.demo.neurade.configs.RabbitMQConfig;
import app.demo.neurade.domain.dtos.ChatResponseDTO;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.chatbot.Conversation;
import app.demo.neurade.domain.models.chatbot.QAEntry;
import app.demo.neurade.domain.rabbitmq.ChatbotChatJob;
import app.demo.neurade.domain.rabbitmq.JobStatus;
import app.demo.neurade.infrastructures.chatbot_llm.ChatbotClient;
import app.demo.neurade.infrastructures.chatbot_llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import app.demo.neurade.infrastructures.repositories.ConversationRepository;
import app.demo.neurade.infrastructures.repositories.QAEntryRepository;
import app.demo.neurade.infrastructures.repositories.UserRepository;
import app.demo.neurade.services.ChatbotPersistenceService;
import app.demo.neurade.services.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatbotChatWorker {
    private final JobStatusService jobStatusService;
    private final QAEntryRepository qaEntryRepository;
    private final ChatbotClient chatbotClient;
    private final ConversationRepository conversationRepository;
    private final ChatbotPersistenceService chatbotPersistenceService;
    private final UserRepository userRepository;

    @Value("${llm.top-k}")
    private int topK;

    @RabbitListener(queues = RabbitMQConfig.CHATBOT_QUEUE)
    public void processChatbotChatJob(ChatbotChatJob job) {
        try {
            log.info("Processing ChatbotChatJob with ID: {}", job.getJobId());

            job.setStatus(JobStatus.PROCESSING);
            jobStatusService.saveJob(job);

            Conversation conversation = conversationRepository.findById(job.getConversationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Conversation not found with ID: " + job.getConversationId()
                    ));

            log.info("Calling workflow for ChatbotChatJob with ID: {}", job.getJobId());
            log.info("Question: {}", job.getQuestion());

            WorkflowResponse workflowResponse = callWorkflow(
                    conversation,
                    job.getQuestion(),
                    job.getApiKey(),
                    job.getFileUrls()
            );

            handleResponse(job, workflowResponse);
            job.setStatus(JobStatus.COMPLETED);
            jobStatusService.saveJob(job);
            log.info("Completed ChatbotChatJob with ID: {}", job.getJobId());

        } catch (Exception e) {
            log.error("Error processing ChatbotChatJob with ID: {}", job.getJobId(), e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobStatusService.saveJob(job);
            throw e;
        }
    }

    private void handleResponse(ChatbotChatJob job, WorkflowResponse workflowResponse) {
        User user = userRepository.findById(job.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + job.getUserId()
                ));

        boolean hasAssistant = workflowResponse.getAssistant() != null;
        boolean hasGuardian  = workflowResponse.getGuardian() != null;

        if (!hasAssistant && !hasGuardian) {
            throw new IllegalStateException(
                    "No response from LLM (assistant & guardian are null)"
            );
        }

        log.info(
                "LLM response received. Assistant present: {}, Guardian present: {}",
                hasAssistant,
                hasGuardian
        );

        String reply = extractReply(workflowResponse);

        if (reply == null || reply.isBlank()) {
            throw new IllegalStateException("LLM returned empty reply");
        }

        String preview = reply.length() > 100 ? reply.substring(0, 100) + "..." : reply;
        log.info("Extracted reply preview: {}", preview);

        long tokenUsed = extractTokenUsed(workflowResponse);
        log.info("LLM total token used (guardian + assistant): {}", tokenUsed);

        chatbotPersistenceService.finalizeChat(
                user,
                job.getInstanceId(),
                job.getQaEntryId(),
                reply,
                tokenUsed
        );
        ChatResponseDTO dto = ChatResponseDTO.builder()
                .conversationId(job.getConversationId())
                .response(workflowResponse)
                .build();

        job.setResponse(dto);
    }

    private WorkflowResponse callWorkflow(
            Conversation conversation,
            String question,
            String apiKey,
            List<String> assetUrls
    ) {
        List<WorkflowRequest.Query> queries = buildContext(conversation, question);
        return chatbotClient.callWorkflow(queries, apiKey, assetUrls);
    }

    private List<WorkflowRequest.Query> buildContext(
            Conversation conversation,
            String question
    ) {
        List<QAEntry> latestEntries = qaEntryRepository.findLatestByConversation(
                conversation,
                PageRequest.of(0, topK)
        );

        // Remove the first entry if it's empty (the current question)
        if (latestEntries.getFirst().getAnswer() == null ||
                latestEntries.getFirst().getAnswer().isBlank()) {
            latestEntries.removeFirst();
        }

        Collections.reverse(latestEntries);

        List<WorkflowRequest.Query> queries = latestEntries.stream()
                .flatMap(qaEntry -> Stream.of(
                        new WorkflowRequest.Query("user", qaEntry.getQuestionText() != null ? qaEntry.getQuestionText() : ""),
                        new WorkflowRequest.Query("assistant", qaEntry.getAnswer())
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        queries.add(new WorkflowRequest.Query("user", question));

        return queries;
    }

    private String extractReply(WorkflowResponse res) {

        if (res.getAssistantRaw() != null &&
                res.getAssistantRaw().getContent() != null &&
                !res.getAssistantRaw().getContent().isBlank()) {

            return res.getAssistantRaw().getContent();
        }

        if (res.getGuardian() != null &&
                res.getGuardian().getReply() != null &&
                !res.getGuardian().getReply().isBlank()) {

            return res.getGuardian().getReply();
        }

        return null;
    }

    private long extractTokenUsed(WorkflowResponse res) {
        long total = 0L;

        if (res.getGuardianRaw() != null &&
                res.getGuardianRaw().getUsageMetadata() != null) {

            total += res.getGuardianRaw()
                    .getUsageMetadata()
                    .getTotalTokens();
        }

        if (res.getAssistantRaw() != null &&
                res.getAssistantRaw().getUsageMetadata() != null) {

            total += res.getAssistantRaw()
                    .getUsageMetadata()
                    .getTotalTokens();
        }

        return total;
    }
}
