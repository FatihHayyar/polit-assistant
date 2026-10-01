package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.ai.OllamaClient;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class ChatIntentParser {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChatIntentParser.class);

    private static final int DEFAULT_LIMIT = 5;

    private static final Set<String> VALID_BODY_KEYS = Set.of(
            "ZH", "BE", "LU", "UR", "SZ", "OW", "NW", "GL",
            "ZG", "FR", "SO", "BS", "BL", "SH", "AR", "AI",
            "SG", "GR", "AG", "TG", "TI", "VD", "VS", "NE",
            "GE", "JU"
    );

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;

    public ChatIntentParser(
            OllamaClient ollamaClient
    ) {
        this.ollamaClient = ollamaClient;
        this.objectMapper = new ObjectMapper();
    }

    public ChatQuery parse(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Die Frage darf nicht leer sein."
            );
        }

        try {

            String json =
                    ollamaClient.generateJson(
                            buildPlannerPrompt(question)
                    );

            LOG.info(
                    "Chat planner raw result: {}",
                    json
            );

            PlannerResult result =
                    objectMapper.readValue(
                            json,
                            PlannerResult.class
                    );

            ChatQuery query = toChatQuery(result);

            LOG.info(
                    "Chat planner parsed result: " +
                            "intent={}, topic={}, bodyKey={}, " +
                            "location={}, searchText={}, sort={}, limit={}",
                    query.intent(),
                    query.topic(),
                    query.bodyKey(),
                    query.locationDisplayName(),
                    query.searchText(),
                    query.sortOrder(),
                    query.limit()
            );

            return query;

        } catch (RuntimeException | JsonProcessingException e) {

            LOG.warn(
                    "Chat planner unavailable, using safe fallback: {}",
                    e.getMessage()
            );

            return fallbackQuery(question);
        }
    }

    private ChatQuery toChatQuery(
            PlannerResult result
    ) {

        return new ChatQuery(
                parseIntent(result.operation()),
                parseTopic(result.topic()),
                normalizeBodyKey(result.bodyKey()),
                normalize(result.locationDisplayName()),
                normalize(result.searchText()),
                parseSortOrder(result.sort()),
                result.limit() == null
                        ? DEFAULT_LIMIT
                        : result.limit()
        );
    }

    private ChatQuery fallbackQuery(
            String question
    ) {

        return new ChatQuery(
                ChatQuery.ChatIntent.SUMMARY,
                null,
                null,
                null,
                question.trim(),
                ChatQuery.SortOrder.RELEVANCE,
                DEFAULT_LIMIT
        );
    }

    private ChatQuery.ChatIntent parseIntent(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return ChatQuery.ChatIntent.SUMMARY;
        }

        try {
            return ChatQuery.ChatIntent.valueOf(
                    value.trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException e) {
            return ChatQuery.ChatIntent.SUMMARY;
        }
    }

    private Topic parseTopic(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            Topic topic =
                    Topic.valueOf(
                            value.trim()
                                    .toUpperCase(Locale.ROOT)
                    );

            return topic == Topic.SONSTIGES
                    ? null
                    : topic;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ChatQuery.SortOrder parseSortOrder(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return ChatQuery.SortOrder.NEWEST;
        }

        try {
            return ChatQuery.SortOrder.valueOf(
                    value.trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException e) {
            return ChatQuery.SortOrder.NEWEST;
        }
    }

    private String normalizeBodyKey(
            String bodyKey
    ) {

        String normalized = normalize(bodyKey);

        if (normalized == null) {
            return null;
        }

        normalized =
                normalized.toUpperCase(Locale.ROOT);

        return VALID_BODY_KEYS.contains(normalized)
                ? normalized
                : null;
    }

    private String normalize(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String buildPlannerPrompt(
            String question
    ) {

        return """
                Analysiere die Benutzerfrage für eine Schweizer Parlamentsdatenbank.
                Antworte ausschliesslich mit einem JSON-Objekt.

                Schema:
                {
                  "operation":"COUNT|LIST|SUMMARY",
                  "topic":"ENERGIE|BIODIVERSITAET|WASSER|LANDWIRTSCHAFT|RAUMPLANUNG|KLIMA|MOBILITAET|ABFALL|null",
                  "bodyKey":"Kantonskürzel oder null",
                  "locationDisplayName":"Ort/Kanton oder null",
                  "searchText":"fachliche Suchbegriffe oder null",
                  "sort":"NEWEST|RELEVANCE",
                  "limit":5
                }

                Operation:
                COUNT = Die Frage verlangt eine Anzahl.
                LIST = Die Frage verlangt eine Liste von Geschäften.
                SUMMARY = Die Frage verlangt eine Erklärung,
                Zusammenfassung oder inhaltliche Antwort.

                Sortierung:
                NEWEST = Die Frage enthält einen zeitlichen Fokus,
                zum Beispiel neu, aktuell, zuletzt oder kürzlich.
                RELEVANCE = Sonst nach inhaltlicher Relevanz.

                WWF-Themen:
                ENERGIE
                BIODIVERSITAET
                WASSER
                LANDWIRTSCHAFT
                RAUMPLANUNG
                KLIMA
                MOBILITAET
                ABFALL

                Schweizer Kantone:
                Zürich=ZH
                Bern=BE
                Luzern=LU
                Uri=UR
                Schwyz=SZ
                Obwalden=OW
                Nidwalden=NW
                Glarus=GL
                Zug=ZG
                Freiburg=FR
                Fribourg=FR
                Solothurn=SO
                Basel-Stadt=BS
                Basel-Landschaft=BL
                Schaffhausen=SH
                Appenzell Ausserrhoden=AR
                Appenzell Innerrhoden=AI
                St. Gallen=SG
                Graubünden=GR
                Grisons=GR
                Aargau=AG
                Thurgau=TG
                Tessin=TI
                Ticino=TI
                Waadt=VD
                Vaud=VD
                Wallis=VS
                Valais=VS
                Neuenburg=NE
                Neuchâtel=NE
                Genf=GE
                Genève=GE
                Jura=JU

                Regeln:
                - Wenn ein Kanton genannt wird, setze bodyKey auf das
                  entsprechende Kantonskürzel.
                - locationDisplayName enthält den vom Benutzer genannten
                  Orts- oder Kantonsnamen.
                - Wenn kein Ort oder Kanton genannt wird, sind bodyKey und
                  locationDisplayName null.
                - Ein WWF-Thema darf semantisch erkannt werden.
                - Gewässerschutz, Gewässer, Wasser und Fliessgewässer gehören
                  zum WWF-Thema WASSER.
                - Allgemeine Wörter wie "Geschäft", "aktuell", "zeigen",
                  "erklären" oder "parlamentarisch" gehören nicht in searchText.
                - searchText enthält nur zusätzliche fachliche Begriffe,
                  die für die Datenbanksuche relevant sind.
                - Erfinde keine Filter oder Orte.
                - Gib keine Erklärung ausserhalb des JSON-Objekts aus.

                Benutzerfrage:
                %s
                """.formatted(question);
    }

    private record PlannerResult(
            String operation,
            String topic,
            String bodyKey,
            String locationDisplayName,
            String searchText,
            String sort,
            Integer limit
    ) {
    }
}