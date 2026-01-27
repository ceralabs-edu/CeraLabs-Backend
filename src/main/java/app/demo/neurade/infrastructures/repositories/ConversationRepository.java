package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.chatbot.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
}
