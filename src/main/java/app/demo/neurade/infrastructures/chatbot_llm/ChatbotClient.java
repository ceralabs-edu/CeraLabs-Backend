package app.demo.neurade.infrastructures.chatbot_llm;

import app.demo.neurade.infrastructures.chatbot_llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.chatbot_llm.responses.WorkflowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class ChatbotClient {

    private final WebClient webClient;

    @Value("${llm.model}")
    private String model;

    @Value("${llm.timeout}")
    private int timeoutSeconds;

    public ChatbotClient(@Value("${llm.qa.endpoint}") String workflowUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(workflowUrl)
                .build();
    }

    public WorkflowResponse callWorkflow(
            List<WorkflowRequest.Query> queries,
            String apiKey,
            List<String> assetUrls
    ) {
        WorkflowRequest request = WorkflowRequest.builder()
                .apiKey(apiKey)
                .model(model)
                .files(assetUrls)
                .queries(queries)
                .build();

        try {
            WorkflowResponse response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(WorkflowResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response == null) {
                throw new IllegalStateException("Invalid response from LLM");
            }

            return response;

        } catch (WebClientResponseException ex) {
            log.error("LLM returned error: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("LLM error: " + ex.getResponseBodyAsString(), ex);

        } catch (WebClientRequestException ex) {
            throw new RuntimeException("LLM timeout or connection error", ex);
        }
    }
}
