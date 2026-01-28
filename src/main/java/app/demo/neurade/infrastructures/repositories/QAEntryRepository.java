package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.chatbot.Conversation;
import app.demo.neurade.domain.models.chatbot.QAEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QAEntryRepository extends JpaRepository<QAEntry, Long> {
    @Query("""
        select q from QAEntry q
        where q.conversation = :conversation
        order by q.createdAt desc
    """)
    List<QAEntry> findLatestByConversation(
            @Param("conversation") Conversation conversation,
            Pageable pageable
    );
}