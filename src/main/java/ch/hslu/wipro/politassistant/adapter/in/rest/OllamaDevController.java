package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.ai.OllamaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dev/ollama")
public class OllamaDevController {

    private final OllamaClient ollamaClient;

    public OllamaDevController(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    @GetMapping("/test")
    public Map<String, String> test() {

        String response = ollamaClient.generate(
                "Antworte auf Deutsch in genau einem kurzen Satz: Was ist ein parlamentarisches Geschäft?"
        );

        return Map.of(
                "model", "qwen2.5:3b",
                "response", response
        );
    }
}