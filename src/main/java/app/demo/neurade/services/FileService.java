package app.demo.neurade.services;

import app.demo.neurade.domain.dtos.ChatAssetUploadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public interface FileService {
    String uploadPfp(MultipartFile file);
    List<ChatAssetUploadDTO> uploadChatAssets(
            String conversationId,
            List<MultipartFile> files
    );
    String uploadBase64Image(String base64DataUrl, String objectKeyPrefix);
    List<String> uploadAssignmentAnswers(UUID questionId, List<MultipartFile> files);
    String uploadAssignmentConcatedImage(UUID questionId, BufferedImage image);
}
