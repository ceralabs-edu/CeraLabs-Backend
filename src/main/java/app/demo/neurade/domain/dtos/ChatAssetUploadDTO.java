package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.chatbot.AssetType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatAssetUploadDTO {
    private AssetType type;
    private String objectUrl;
    private String mimeType;
    private Integer orderIndex;
}
