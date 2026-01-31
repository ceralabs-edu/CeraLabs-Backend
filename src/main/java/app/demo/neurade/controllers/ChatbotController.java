package app.demo.neurade.controllers;

import app.demo.neurade.domain.dtos.requests.ChatRequest;
import app.demo.neurade.domain.mappers.Mapper;
import app.demo.neurade.exception.UnauthorizedException;
import app.demo.neurade.security.CustomUserDetails;
import app.demo.neurade.services.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Chatbot interaction APIs")
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final Mapper mapper;

    @Operation(
            summary = "Chat with AI chatbot",
            description = "Send a question to chatbot with optional uploaded files",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chat response returned successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping(
            value = "/chat",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> chat(
            @Parameter(
                    description = "Chat request payload (JSON)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChatRequest.class)
                    )
            )
            @RequestPart("data") ChatRequest request,

            @Parameter(
                    description = "Optional uploaded files",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart(value = "files", required = false)
            List<MultipartFile> files
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (userDetails == null) throw new UnauthorizedException("Unauthorized");

        return ResponseEntity.ok(
                chatbotService.chat(
                        userDetails.getUser(),
                        request.getInstanceId(),
                        request.getConversationId(),
                        request.getQuestion(),
                        files
                )
        );
    }

    @GetMapping("/{conversationId}/history")
    public ResponseEntity<?> getChatHistory(
            @PathVariable String conversationId
    ) {
        return ResponseEntity.ok(
                chatbotService.getChatHistory(conversationId)
        );
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getUserConversations() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (userDetails == null) throw new UnauthorizedException("Unauthorized");

        return ResponseEntity.ok(
                chatbotService.getUserConversations(userDetails.getUser().getId())
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }
}
