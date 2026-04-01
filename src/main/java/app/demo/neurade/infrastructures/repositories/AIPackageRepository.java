package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.AIPackage;
import app.demo.neurade.domain.models.chatbot.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AIPackageRepository extends JpaRepository<AIPackage, Integer> {
    Optional<AIPackage> findByName(String name);

    @Query("""
    SELECT ap.id
        FROM AIPackage ap
        JOIN Conversation c ON c.instance.aiPackage = ap
        WHERE c.id = :#{#conversation.id}
    """
    )
    String findAIPackageIdFromConversation(Conversation conversation);
}
