package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.ChatAssetUploadDTO;
import app.demo.neurade.domain.dtos.ChatPrepareDTO;
import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserAIInstanceUsage;
import app.demo.neurade.domain.models.chatbot.Conversation;
import app.demo.neurade.domain.models.chatbot.QAEntry;
import app.demo.neurade.domain.models.chatbot.QuestionAsset;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.infrastructures.repositories.ConversationRepository;
import app.demo.neurade.infrastructures.repositories.QAEntryRepository;
import app.demo.neurade.infrastructures.repositories.QuestionAssetRepository;
import app.demo.neurade.infrastructures.repositories.UserInstanceUsageRepository;
import app.demo.neurade.services.ChatbotTxService;
import app.demo.neurade.services.FileService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotTxServiceImpl implements ChatbotTxService {

    private final UserInstanceUsageRepository userInstanceUsageRepository;
    private final QAEntryRepository qaEntryRepository;
    private final ConversationRepository conversationRepository;
    private final FileService fileService;
    private final QuestionAssetRepository questionAssetRepository;

    @Override
    @Transactional
    public ChatPrepareDTO prepareChat(
            User user,
            UUID instanceId,
            String conversationId,
            String question,
            List<MultipartFile> files
    ) {
        UserAIInstanceUsage usage = userInstanceUsageRepository
                .findForUpdate(user, instanceId)
                .orElseThrow(() ->
                        new UnauthorizedException("No usage record found for user and instance")
                );

        if (!usage.canUseThisPackage()) {
            throw new RuntimeException("User has exceeded their AI package usage limits");
        }

        Conversation conversation = getOrCreateConversation(conversationId);

        QAEntry qaEntry = qaEntryRepository.save(
                QAEntry.builder()
                        .conversation(conversation)
                        .questionText(question)
                        .build()
        );

        log.info("Uploading {} files for question", files != null ? files.size() : 0);
        List<String> assetUrls = uploadFilesAndGetUrls(conversation.getId(), qaEntry, files);
        log.info("Uploaded files. Asset URLs: {}", assetUrls);

        log.info("Selecting a random api key from AI package");

        AIPackage aiPackage = usage.getInstance().getAiPackage();
        String apiKey = aiPackage.getRandomApiKey();

        if (apiKey == null) {
            throw new RuntimeException("No API key available in the AI package");
        }

        return ChatPrepareDTO.builder()
                .conversation(conversation)
                .qaEntryId(qaEntry.getId())
                .assetUrls(assetUrls)
                .apiKey(apiKey)
                .build();
    }

    @Override
    @Transactional
    public void finalizeChat(
            User user,
            UUID instanceId,
            Long qaEntryId,
            String reply,
            long tokenUsed
    ) {
        QAEntry qaEntry = qaEntryRepository.findById(qaEntryId)
                .orElseThrow(() -> new EntityNotFoundException("QA entry not found"));
        qaEntry.setAnswer(reply);
        qaEntryRepository.save(qaEntry);

        UserAIInstanceUsage usage = userInstanceUsageRepository
                .findForUpdate(user, instanceId)
                .orElseThrow(() ->
                        new UnauthorizedException("No usage record found for user and instance")
                );
        usage.useToken(tokenUsed);
        userInstanceUsageRepository.save(usage);
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
}
