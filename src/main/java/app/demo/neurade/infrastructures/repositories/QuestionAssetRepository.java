package app.demo.neurade.infrastructures.repositories;

import app.demo.neurade.domain.models.chatbot.QuestionAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAssetRepository extends JpaRepository<QuestionAsset, Long> {
}
