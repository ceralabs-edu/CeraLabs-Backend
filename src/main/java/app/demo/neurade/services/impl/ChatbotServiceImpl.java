package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.ChatHistoryEntryDTO;
import app.demo.neurade.domain.dtos.ChatPrepareDTO;
import app.demo.neurade.domain.dtos.ChatResponseDTO;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.chatbot.Conversation;
import app.demo.neurade.domain.models.chatbot.QAEntry;
import app.demo.neurade.infrastructures.chatbot_llm.ChatbotClient;
import app.demo.neurade.infrastructures.chatbot_llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import app.demo.neurade.infrastructures.repositories.ConversationRepository;
import app.demo.neurade.infrastructures.repositories.QAEntryRepository;
import app.demo.neurade.services.ChatbotService;
import app.demo.neurade.services.ChatbotPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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

    private final ChatbotClient chatbotClient;
    private final QAEntryRepository qaEntryRepository;
    private final ChatbotPersistenceService chatbotPersistenceService;
    private final ConversationRepository conversationRepository;
    private final Mapper mapper;


    @Value("${llm.top-k}")
    private int topK;

    @Override
    public ChatResponseDTO chat(
            User user,
            UUID instanceId,
            String conversationId,
            String question,
            List<MultipartFile> files
    ) {
        /*
         * Phase 1: Validate and prepare (in transaction)
         */
        ChatPrepareDTO prepareResult = chatbotPersistenceService.prepareChat(user, instanceId, conversationId, question, files);
        instanceId = prepareResult.instanceId();
        /*
         * Phase 2: Call workflow (outside transaction - no DB lock held)
         */
        log.info("Calling workflow for question: {}", question);
        WorkflowResponse workflowResponse = callWorkflow(
                prepareResult.conversation(),
                question,
                prepareResult.apiKey(),
                prepareResult.assetUrls()
        );

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

        /*
         * Phase 3: Update results (in transaction)
         */
        long tokenUsed = extractTokenUsed(workflowResponse);
        log.info("LLM total token used (guardian + assistant): {}", tokenUsed);

        chatbotPersistenceService.finalizeChat(user, instanceId, prepareResult.qaEntryId(), reply, tokenUsed);

        return ChatResponseDTO.builder()
                .conversationId(prepareResult.conversation().getId())
                .response(workflowResponse)
                .build();
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

    @Override
    public List<ChatHistoryEntryDTO> getChatHistory(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }
        List<QAEntry> entries = qaEntryRepository.findAllByConversationIdWithAssets(conversationId);
        return entries.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findAllByUser_Id(userId);
    }
}
