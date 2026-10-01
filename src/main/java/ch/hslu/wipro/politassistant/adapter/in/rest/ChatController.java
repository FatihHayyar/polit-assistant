package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.ChatRequest;
import ch.hslu.wipro.politassistant.adapter.in.rest.dto.ChatResponse;
import ch.hslu.wipro.politassistant.application.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(
        name = "Chat",
        description = "Dialogorientierte Suche nach parlamentarischen Geschäften"
)
class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Polit-Assistant fragen",
            description = """
                    Ermöglicht eine einfache dialogorientierte Suche.

                    Der Polit-Assistant erkennt bekannte WWF-Themen
                    in einer natürlich formulierten Frage und zeigt
                    passende parlamentarische Geschäfte an.

                    Wird kein WWF-Thema erkannt, wird die Frage als
                    Volltextsuche verwendet.
                    """
    )
    @PostMapping
    ChatResponse ask(
            @RequestBody ChatRequest request
    ) {
        return chatService.ask(request.question());
    }
}