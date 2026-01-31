package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.ChatAssetUploadDTO;
import app.demo.neurade.domain.models.chatbot.AssetType;
import app.demo.neurade.exception.StorageException;
import app.demo.neurade.services.FileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
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

    @Value("${minio.bucket.assignment}")
    private String assignmentBucket;

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
            List<MultipartFile> files
    ) {
        List<ChatAssetUploadDTO> results = new ArrayList<>();
        int order = 1;

        for (MultipartFile file : files) {
            String objectKey = buildChatObjectKey(
                    conversationId,
                    order,
                    file.getOriginalFilename()
            );

            putObject(file, chatBucket, objectKey);

            results.add(ChatAssetUploadDTO.builder()
                    .type(resolveAssetType(file.getContentType()))
                    .objectUrl(
                            buildObjectUrl(chatBucket, objectKey)
                    )
                    .mimeType(file.getContentType())
                    .orderIndex(order)
                    .build());

            order++;
        }

        return results;
    }

    @Override
    public String uploadBase64Image(String base64DataUrl, String objectKeyPrefix) {
        // Extract base64 data from data URL
        String base64 = base64DataUrl.substring(base64DataUrl.indexOf(",") + 1);
        byte[] bytes = Base64.getDecoder().decode(base64);

        // Determine content type from data URL
        String contentType = "image/png"; // default
        if (base64DataUrl.startsWith("data:")) {
            int commaIndex = base64DataUrl.indexOf(",");
            String mimeType = base64DataUrl.substring(5, commaIndex);
            if (mimeType.contains(";")) {
                mimeType = mimeType.substring(0, mimeType.indexOf(";"));
            }
            if (!mimeType.isEmpty()) {
                contentType = mimeType;
            }
        }

        // Determine file extension
        String extension = "png";
        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            extension = "jpg";
        } else if (contentType.contains("gif")) {
            extension = "gif";
        } else if (contentType.contains("webp")) {
            extension = "webp";
        }

        // Generate unique object key
        String objectKey = String.format(
                "%s/%s.%s",
                objectKeyPrefix,
                UUID.randomUUID(),
                extension
        );

        // Upload to MinIO using existing infrastructure
        createBucketIfNotExists(assignmentBucket);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(assignmentBucket)
                            .object(objectKey)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to upload base64 image to MinIO: " + e.getMessage());
        }

        return buildObjectUrl(assignmentBucket, objectKey);
    }

    private String buildChatObjectKey(
            String conversationId,
            int order,
            String originalFilename
    ) {
        return String.format(
                "chat/%s/%s/%02d-%s",
                conversationId,
                UUID.randomUUID(),
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
        }
        ensurePublicReadPolicy(bucketName);
    }

    private void ensurePublicReadPolicy(String bucketName) {
        String policy = """
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": "*",
              "Action": ["s3:GetObject"],
              "Resource": ["arn:aws:s3:::%s/*"]
            }
          ]
        }
        """.formatted(bucketName);

        try {
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to set bucket policy: " + e.getMessage());
        }
    }

    private String presignGetObject(
            String bucket,
            String objectKey,
            int expirySeconds
    ) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(expirySeconds) // seconds
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Failed to generate presigned URL: " + e.getMessage());
        }
    }

    private String buildAssignmentAnswerObjectKey(
            UUID questionId,
            String originalFilename
    ) {
        return String.format(
                "assignment/%s/%s-%s",
                questionId,
                UUID.randomUUID(),
                originalFilename
        );
    }

    @Override
    public List<String> uploadAssignmentAnswers(
            UUID questionId,
            List<MultipartFile> files
    ) {
        List<String> objectUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String objectKey = buildAssignmentAnswerObjectKey(
                    questionId,
                    file.getOriginalFilename()
            );

            putObject(file, assignmentBucket, objectKey);

            objectUrls.add(buildObjectUrl(assignmentBucket, objectKey));
        }

        return objectUrls;
    }

    @Override
    public String uploadAssignmentConcatedImage(UUID questionId, BufferedImage image) {
        // Generate unique object key for the concatenated image
        String objectKey = String.format(
                "assignment/%s/%s-concatenated.png",
                questionId,
                UUID.randomUUID()
        );

        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // Upload to MinIO
            createBucketIfNotExists(assignmentBucket);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(assignmentBucket)
                            .object(objectKey)
                            .stream(new ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                            .contentType("image/png")
                            .build()
            );

            return buildObjectUrl(assignmentBucket, objectKey);
        } catch (Exception e) {
            throw new StorageException("Failed to upload concatenated image to MinIO: " + e.getMessage());
        }
    }

}
