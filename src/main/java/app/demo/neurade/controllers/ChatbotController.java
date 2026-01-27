package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.requests.ChatRequest;
import app.demo.neurade.services.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> chat(
            @RequestPart("data") ChatRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.ok(
                chatbotService.chat(
                        request.getConversationId(),
                        request.getQuestion(),
                        files
                )
        );
    }
}
