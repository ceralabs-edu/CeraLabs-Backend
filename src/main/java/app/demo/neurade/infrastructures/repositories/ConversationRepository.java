package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.chatbot.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
    List<Conversation> findAllByUser_Id(Long userId);
}
