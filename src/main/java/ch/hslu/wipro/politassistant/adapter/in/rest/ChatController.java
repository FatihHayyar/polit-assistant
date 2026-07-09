package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.ChatRequest;
import ch.hslu.wipro.politassistant.adapter.in.rest.dto.ChatResponse;
import ch.hslu.wipro.politassistant.application.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat", description = "Chatbot-style search interface")
class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    @Operation(
            summary = "Ask the Polit Assistant",
            description = "Chatbot-style endpoint that detects a topic from a natural language question and returns matching parliamentary affairs."
    )
    @PostMapping
    ChatResponse ask(@RequestBody ChatRequest request) {
        return chatService.ask(request.question());
    }
}