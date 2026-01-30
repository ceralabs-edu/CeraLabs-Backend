package app.demo.neurade.domain.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryEntryDTO {
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private List<ChatHistoryEntryAssetDTO> assets;
}
