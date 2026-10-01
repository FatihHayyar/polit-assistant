package ch.hslu.wipro.politassistant.adapter.out.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class OllamaClient {

    private static final Logger LOG =
            LoggerFactory.getLogger(OllamaClient.class);

    private static final int PLANNER_MAX_OUTPUT_TOKENS = 160;
    private static final int PLANNER_CONTEXT_WINDOW = 4096;

    private static final int ANSWER_MAX_OUTPUT_TOKENS = 100;
    private static final int ANSWER_CONTEXT_WINDOW = 4096;

    private static final Duration CONNECT_TIMEOUT =
            Duration.ofSeconds(3);

    private static final Duration REQUEST_TIMEOUT =
            Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final URI generateUri;
    private final String model;

    public OllamaClient(
            @Value("${app.ollama.base-url}") String baseUrl,
            @Value("${app.ollama.model}") String model
    ) {

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();

        this.objectMapper = new ObjectMapper();

        this.generateUri = URI.create(
                normalizeBaseUrl(baseUrl) + "/api/generate"
        );

        this.model = model;
    }

    public String generate(String prompt) {

        return execute(
                new GenerateRequest(
                        model,
                        prompt,
                        false,
                        false,
                        null,
                        new Options(
                                ANSWER_MAX_OUTPUT_TOKENS,
                                ANSWER_CONTEXT_WINDOW,
                                0.1
                        )
                ),
                "answer"
        );
    }

    public String generateJson(String prompt) {

        return execute(
                new GenerateRequest(
                        model,
                        prompt,
                        false,
                        false,
                        "json",
                        new Options(
                                PLANNER_MAX_OUTPUT_TOKENS,
                                PLANNER_CONTEXT_WINDOW,
                                0.0
                        )
                ),
                "planner"
        );
    }

    private String execute(
            GenerateRequest request,
            String operation
    ) {

        long start = System.nanoTime();

        try {

            String requestJson =
                    objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(generateUri)
                    .timeout(REQUEST_TIMEOUT)
                    .header(
                            "Content-Type",
                            "application/json"
                    )
                    .header(
                            "Accept",
                            "application/json"
                    )
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    requestJson
                            )
                    )
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Ollama HTTP "
                                + response.statusCode()
                                + ": "
                                + abbreviate(response.body())
                );
            }

            String rawResponse = response.body();

            if (rawResponse == null
                    || rawResponse.isBlank()) {

                throw new IllegalStateException(
                        "Ollama hat keine Antwort zurückgegeben."
                );
            }

            JsonNode root =
                    objectMapper.readTree(rawResponse);

            JsonNode responseNode =
                    root.get("response");

            if (responseNode == null
                    || responseNode.isNull()
                    || responseNode.asText().isBlank()) {

                throw new IllegalStateException(
                        "Ollama hat keine verwertbare Antwort zurückgegeben. "
                                + "Antwort: "
                                + abbreviate(rawResponse)
                );
            }

            String result =
                    responseNode.asText().trim();

            LOG.info(
                    "Ollama {} completed in {} ms: "
                            + "status={}, inputCharacters={}, "
                            + "outputCharacters={}",
                    operation,
                    elapsedMillis(start),
                    response.statusCode(),
                    request.prompt().length(),
                    result.length()
            );

            return result;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            LOG.warn(
                    "Ollama {} interrupted after {} ms",
                    operation,
                    elapsedMillis(start)
            );

            throw new IllegalStateException(
                    "Die Ollama-Anfrage wurde unterbrochen.",
                    e
            );

        } catch (Exception e) {

            LOG.warn(
                    "Ollama {} failed after {} ms: {}",
                    operation,
                    elapsedMillis(start),
                    e.getMessage()
            );

            throw new IllegalStateException(
                    "Die Ollama-Anfrage ist fehlgeschlagen.",
                    e
            );
        }
    }

    private String normalizeBaseUrl(
            String baseUrl
    ) {

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Die Ollama Base-URL darf nicht leer sein."
            );
        }

        String normalized = baseUrl.trim();

        while (normalized.endsWith("/")) {
            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        return normalized;
    }

    private String abbreviate(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String normalized =
                value.replaceAll("\\s+", " ").trim();

        if (normalized.length() <= 500) {
            return normalized;
        }

        return normalized.substring(0, 500) + " …";
    }

    private long elapsedMillis(
            long startNano
    ) {

        return (
                System.nanoTime() - startNano
        ) / 1_000_000;
    }

    private record GenerateRequest(
            String model,
            String prompt,
            boolean stream,
            boolean think,
            String format,
            Options options
    ) {
    }

    private record Options(
            int num_predict,
            int num_ctx,
            double temperature
    ) {
    }
}