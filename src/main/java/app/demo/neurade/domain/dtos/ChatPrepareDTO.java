package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.chatbot.Conversation;

import java.util.List;

public record ChatPrepareDTO(
        Conversation conversation,
        Long qaEntryId,
        List<String> assetUrls
) {}