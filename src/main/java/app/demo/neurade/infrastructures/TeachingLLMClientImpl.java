package app.demo.neurade.infrastructures;

import app.demo.neurade.infrastructures.llm.TeachingLLMClient;
import app.demo.neurade.infrastructures.llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.llm.responses.WorkflowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TeachingLLMClientImpl implements TeachingLLMClient {

    private final RestTemplate restTemplate;

    @Value("${llm.qa-endpoint}")
    private String url;

    @Override
    public WorkflowResponse chat(WorkflowRequest request) {
        try {
            return restTemplate.postForObject(
                    url,
                    request,
                    WorkflowResponse.class
            );
        } catch (Exception e) {
            throw new RuntimeException("Teaching LLM call failed", e);
        }
    }
}
