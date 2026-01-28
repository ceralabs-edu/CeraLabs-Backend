package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.ChatAssetUploadDTO;
import app.demo.neurade.domain.dtos.ChatResponseDTO;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserAIInstanceUsage;
import app.demo.neurade.domain.models.chatbot.Conversation;
import app.demo.neurade.domain.models.chatbot.QAEntry;
import app.demo.neurade.domain.models.chatbot.QuestionAsset;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.infrastructures.llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.llm.responses.WorkflowResponse;
import app.demo.neurade.infrastructures.repositories.ConversationRepository;
import app.demo.neurade.infrastructures.repositories.QAEntryRepository;
import app.demo.neurade.infrastructures.repositories.QuestionAssetRepository;
import app.demo.neurade.infrastructures.repositories.UserInstanceUsageRepository;
import app.demo.neurade.services.ChatbotService;
import app.demo.neurade.services.FileService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private final ConversationRepository conversationRepository;
    private final RestTemplate restTemplate;
    private final QAEntryRepository qaEntryRepository;
    private final UserInstanceUsageRepository userInstanceUsageRepository;
    private final FileService fileService;
    private final QuestionAssetRepository questionAssetRepository;

    @Value("${llm.qa.endpoint}")
    private String workflowUrl;

    @Value("${llm.api-key}")
    private String apiKey;

    @Value("${llm.model}")
    private String model;

    @Value("${llm.top-k}")
    private int topK;

    @Override
    @Transactional
    public ChatResponseDTO chat(
            User user,
            UUID instanceId,
            String conversationId,
            String question,
            List<MultipartFile> files
    ) {
        /*
            *
            * Check user usage limits
            *
         */
        UserAIInstanceUsage usage = userInstanceUsageRepository
                .findForUpdate(user, instanceId)
                .orElseThrow(() ->
                        new UnauthorizedException("No usage record found for user and instance")
                );

        if (!usage.canUseThisPackage()) {
            throw new RuntimeException("User has exceeded their AI package usage limits");
        }

        /*
         *
         * Get or create conversation
         *
         */

        Conversation conversation = getOrCreateConversation(conversationId);

        /*
         *
         * Save question entry
         *
         */

        QAEntry qaEntry = qaEntryRepository.save(
                QAEntry.builder()
                .conversation(conversation)
                .questionText(question)
                .build()
        );

        /*
         *
         * Upload files and get URLs
         *
         */
        log.info("Uploading {} files for question", files != null ? files.size() : 0);
        List<String> assetUrls = uploadFilesAndGetUrls(conversation.getId(), qaEntry, files);
        log.info("Uploaded files. Asset URLs: {}", assetUrls);

        /*
         *
         * Call workflow
         *
         */
        log.info("Calling workflow for question: {}", question);
        WorkflowResponse workflowResponse = callWorkflow(conversation, question, assetUrls);

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

        /*
            *
            * Extract reply from workflow response
            *
         */
        String reply = extractReply(workflowResponse);

        if (reply == null || reply.isBlank()) {
            throw new IllegalStateException("LLM returned empty reply");
        }

        String preview = reply.length() > 100 ? reply.substring(0, 100) + "..." : reply;
        log.info("Extracted reply preview: {}", preview);

        /*
         *
         * Update QA entry with answer
         *
         */
        qaEntry.setAnswer(reply);
        qaEntryRepository.save(qaEntry);

        /*
         *
         * Update usage statistics
         *
         */

        long tokenUsed = extractTokenUsed(workflowResponse);

        log.info("LLM total token used (guardian + assistant): {}", tokenUsed);

        usage.useToken(tokenUsed);

        userInstanceUsageRepository.save(usage);

        return ChatResponseDTO.builder()
                .conversationId(conversation.getId())
                .response(workflowResponse)
                .build();
    }

    private List<String> uploadFilesAndGetUrls(
            String conversationId,
            QAEntry qaEntry,
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChatAssetUploadDTO> uploaded = fileService.uploadChatAssets(
                conversationId,
                files
        );

        List<QuestionAsset> assets = uploaded.stream()
                .map(dto -> QuestionAsset.builder()
                        .qaEntry(qaEntry)
                        .type(dto.getType())
                        .objectUrl(dto.getObjectUrl())
                        .mimeType(dto.getMimeType())
                        .orderIndex(dto.getOrderIndex())
                        .build()
                )
                .toList();

        questionAssetRepository.saveAll(assets);

        return uploaded.stream()
                .map(ChatAssetUploadDTO::getObjectUrl)
                .toList();
    }


    private Conversation getOrCreateConversation(String conversationId) {
        if (conversationId == null) {
            return conversationRepository.save(
                    Conversation.builder().build()
            );
        }

        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found with id: " + conversationId));
    }

    private WorkflowResponse callWorkflow(
            Conversation conversation,
            String question,
            List<String> assetUrls
    ) {

        List<WorkflowRequest.Query> queries = buildContext(conversation, question);

        WorkflowRequest request = WorkflowRequest.builder()
                .apiKey(apiKey)
                .model(model)
                .files(assetUrls)
                .queries(queries)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WorkflowRequest> entity =
                new HttpEntity<>(request, headers);

        try {
            ResponseEntity<WorkflowResponse> response =
                    restTemplate.exchange(
                            workflowUrl,
                            HttpMethod.POST,
                            entity,
                            WorkflowResponse.class
                    );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("Invalid response from LLM");
            }

            return response.getBody();

        } catch (HttpStatusCodeException ex) {
            log.error("LLM returned error: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("LLM error: " + ex.getResponseBodyAsString(), ex);

        } catch (ResourceAccessException ex) {
            throw new RuntimeException("LLM timeout or connection error", ex);
        }
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
