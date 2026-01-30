package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.chatbot.Conversation;
import lombok.*;

import java.util.List;

@Builder
public record ChatPrepareDTO(
        Conversation conversation,
        Long qaEntryId,
        String apiKey,
        List<String> assetUrls
) {}