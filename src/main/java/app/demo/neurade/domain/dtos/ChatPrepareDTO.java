package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.chatbot.Conversation;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
public record ChatPrepareDTO(
        Conversation conversation,
        UUID instanceId,
        Long qaEntryId,
        String apiKey,
        List<String> assetUrls
) {}