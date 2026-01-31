package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.chatbot.AssetType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAssetUploadDTO {
    private AssetType type;
    private String objectUrl;
    private String mimeType;
    private Integer orderIndex;
}
