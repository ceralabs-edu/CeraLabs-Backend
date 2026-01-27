package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.chatbot.QAEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QAEntryRepository extends JpaRepository<QAEntry, Long> {
}