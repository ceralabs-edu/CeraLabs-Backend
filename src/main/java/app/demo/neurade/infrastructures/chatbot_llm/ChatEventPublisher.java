package app.demo.neurade.infrastructures.chatbot_llm;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatEventPublisher {

    // jobId → SseEmitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String jobId) {
        SseEmitter emitter = new SseEmitter(Duration.ofMinutes(10).toMillis());
        emitters.put(jobId, emitter);
        emitter.onCompletion(() -> emitters.remove(jobId));
        emitter.onTimeout(() -> emitters.remove(jobId));
        return emitter;
    }

    public void publish(String jobId, String eventType, Object data) {
        SseEmitter emitter = emitters.get(jobId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name(eventType).data(data));
        } catch (IOException e) {
            emitters.remove(jobId);
        }
    }

    public void complete(String jobId) {
        SseEmitter emitter = emitters.get(jobId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(jobId);
        }
    }
}