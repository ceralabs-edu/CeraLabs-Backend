package app.demo.neurade.infrastructures.llm;

import app.demo.neurade.infrastructures.llm.requests.WorkflowRequest;
import app.demo.neurade.infrastructures.llm.responses.WorkflowResponse;

public interface TeachingLLMClient {
    WorkflowResponse chat(WorkflowRequest request);
}
