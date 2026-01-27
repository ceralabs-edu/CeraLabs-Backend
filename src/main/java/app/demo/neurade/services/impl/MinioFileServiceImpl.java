package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.ChatAssetUploadDTO;
import app.demo.neurade.domain.models.chatbot.AssetType;
import app.demo.neurade.exception.StorageException;
import app.demo.neurade.services.FileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.pfp}")
    private String pfpBucket;

    @Value("${minio.bucket.chat}")
    private String chatBucket;

    @Value("${minio.url}")
    private String minioUrl;

    @Override
    public String uploadPfp(MultipartFile file) {
        String objectKey = buildPfpObjectKey(file.getOriginalFilename());
        putObject(file, pfpBucket, objectKey);
        return buildObjectUrl(pfpBucket, objectKey);
    }


    @Override
    public List<ChatAssetUploadDTO> uploadChatAssets(
            String conversationId,
            Long qaId,
            List<MultipartFile> files
    ) {
        List<ChatAssetUploadDTO> results = new ArrayList<>();
        int order = 1;

        for (MultipartFile file : files) {
            String objectKey = buildChatObjectKey(
                    conversationId,
                    qaId,
                    order,
                    file.getOriginalFilename()
            );

            putObject(file, chatBucket, objectKey);

            results.add(ChatAssetUploadDTO.builder()
                    .type(resolveAssetType(file.getContentType()))
                    .objectUrl(buildObjectUrl(chatBucket, objectKey))
                    .mimeType(file.getContentType())
                    .orderIndex(order)
                    .build());

            order++;
        }

        return results;
    }

    private String buildChatObjectKey(
            String conversationId,
            Long qaId,
            int order,
            String originalFilename
    ) {
        return String.format(
                "chat/%s/%s/%02d-%s",
                conversationId,
                qaId,
                order,
                originalFilename
        );
    }

    private String buildPfpObjectKey(String originalFilename) {
        return "pfp/" + UUID.randomUUID() + "-" + originalFilename;
    }

    private String buildObjectUrl(String bucket, String objectKey) {
        return String.format("%s/%s/%s", minioUrl, bucket, objectKey);
    }

    private void putObject(
            MultipartFile file,
            String bucket,
            String objectKey
    ) {
        createBucketIfNotExists(bucket);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to upload Pfp file: " + e.getMessage());
        }
    }


    private AssetType resolveAssetType(String mimeType) {
        if (mimeType != null && mimeType.startsWith("image/")) {
            return AssetType.IMAGE;
        }
        return AssetType.PDF;
    }

    private void createBucketIfNotExists(String bucketName) {
        boolean exists;
        try {
            exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to check if bucket exists: " + e.getMessage());
        }

        if (!exists) {
            try {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            } catch (Exception e) {
                throw new StorageException("Failed to create bucket: " + e.getMessage());
            }
            System.out.println("Bucket created: " + bucketName);
        } else {
            System.out.println("Bucket already exists: " + bucketName);
        }
    }
}
