package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.ChatAssetUploadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    String uploadPfp(MultipartFile file);
    List<ChatAssetUploadDTO> uploadChatAssets(
            String conversationId,
            Long qaId,
            List<MultipartFile> files
    );
}
