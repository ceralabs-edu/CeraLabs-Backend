package app.demo.neurade.infrastructures.assignment_ocr;

import app.demo.neurade.infrastructures.assignment_ocr.responses.ExtractResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@Slf4j
public class AssignmentOcrClient {

    private final WebClient webClient;

    @Value("${llm.timeout}")
    private int timeoutSeconds;

    public AssignmentOcrClient(@Value("${llm.assignment-ocr.endpoint}") String workflowUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(workflowUrl)
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024) // 20MB
                )
                .build();
    }

    public ExtractResponse callExtract(
            MultipartFile file
    ) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", file.getResource())
                .filename(file.getOriginalFilename())
                .contentType(MediaType.APPLICATION_PDF);

        try {
            return webClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RuntimeException(
                                            "OCR service error: " + body
                                    ))
                    )
                    .bodyToMono(ExtractResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();
        } catch (Exception e) {
            log.error("Error calling Assignment OCR service: {}", e.getMessage());
            throw new RuntimeException("Failed to call Assignment OCR service: " + e.getMessage(), e);
        }
    }
}
