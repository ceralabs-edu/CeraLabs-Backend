package app.demo.neurade.domain.dtos;

import app.demo.neurade.domain.models.chatbot.AssetType;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryEntryAssetDTO {
    private AssetType type;

    private String objectUrl;   // MinIO url

    private String mimeType;    // image/png

    private Integer orderIndex; // thứ tự ảnh
}
