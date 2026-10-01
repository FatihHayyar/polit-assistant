package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.adapter.in.rest.dto.ChatResponse;
import ch.hslu.wipro.politassistant.adapter.out.ai.OllamaClient;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairSearchJdbcRepository;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChatService.class);

    private static final int MAX_SUMMARY_RESULTS = 3;

    private final AffairSearchJdbcRepository affairSearchRepository;
    private final ChatIntentParser chatIntentParser;
    private final OllamaClient ollamaClient;

    public ChatService(
            AffairSearchJdbcRepository affairSearchRepository,
            ChatIntentParser chatIntentParser,
            OllamaClient ollamaClient
    ) {
        this.affairSearchRepository = affairSearchRepository;
        this.chatIntentParser = chatIntentParser;
        this.ollamaClient = ollamaClient;
    }

    public ChatResponse ask(String question) {

        long totalStart = System.nanoTime();

        long plannerStart = System.nanoTime();

        ChatQuery query =
                chatIntentParser.parse(question);

        LOG.info(
                "Chat planner completed in {} ms: intent={}, topic={}, bodyKey={}, searchText={}, sort={}, limit={}",
                elapsedMillis(plannerStart),
                query.intent(),
                query.topic(),
                query.bodyKey(),
                query.searchText(),
                query.sortOrder(),
                query.limit()
        );

        ChatResponse response = switch (query.intent()) {
            case COUNT -> handleCount(query);
            case LIST -> handleList(query);
            case SUMMARY -> handleSummary(question, query);
        };

        LOG.info(
                "Chat request completed in {} ms",
                elapsedMillis(totalStart)
        );

        return response;
    }

    private ChatResponse handleCount(ChatQuery query) {

        long start = System.nanoTime();

        long count =
                affairSearchRepository.countForChat(
                        topicName(query.topic()),
                        query.bodyKey(),
                        query.searchText()
                );

        LOG.info(
                "Chat COUNT retrieval completed in {} ms: count={}",
                elapsedMillis(start),
                count
        );

        StringBuilder answer = new StringBuilder();

        answer.append("In den aktuell importierten Daten wurden ")
                .append(count)
                .append(" passende parlamentarische Geschäfte");

        appendDescription(answer, query);

        answer.append(" gefunden.");

        return new ChatResponse(
                answer.toString(),
                displayTopic(query.topic()),
                List.of()
        );
    }

    private ChatResponse handleList(ChatQuery query) {

        long start = System.nanoTime();

        List<AffairSummaryResponse> results =
                search(query, query.limit());

        LOG.info(
                "Chat LIST retrieval completed in {} ms: results={}",
                elapsedMillis(start),
                results.size()
        );

        if (results.isEmpty()) {
            return noResults(query);
        }

        StringBuilder answer =
                new StringBuilder(
                        "Hier sind die passenden parlamentarischen Geschäfte"
                );

        appendDescription(answer, query);
        answer.append(".");

        return new ChatResponse(
                answer.toString(),
                displayTopic(query.topic()),
                results
        );
    }

    private ChatResponse handleSummary(
            String question,
            ChatQuery query
    ) {

        int resultLimit =
                Math.min(
                        query.limit(),
                        MAX_SUMMARY_RESULTS
                );

        long retrievalStart = System.nanoTime();

        List<AffairSummaryResponse> results =
                search(query, resultLimit);

        LOG.info(
                "Chat SUMMARY affair retrieval completed in {} ms: results={}",
                elapsedMillis(retrievalStart),
                results.size()
        );

        if (results.isEmpty()) {
            return noResults(query);
        }

        long contextStart = System.nanoTime();

        String context =
                buildGroundedContext(
                        results,
                        query.searchText()
                );

        LOG.info(
                "Chat context built in {} ms: characters={}",
                elapsedMillis(contextStart),
                context.length()
        );

        String prompt = """
                Du bist der WWF Polit-Assistant.

                Beantworte die Benutzerfrage auf Deutsch ausschliesslich
                anhand der unten bereitgestellten parlamentarischen Daten.

                Regeln:
                - Verwende ausschliesslich die bereitgestellten Daten.
                - Erfinde keine Tatsachen, Geschäfte, Zahlen, Status oder Links.
                - Gib keine politische Empfehlung oder Wahlempfehlung.
                - Die WWF-Themen stammen aus einer regelbasierten
                  Backend-Klassifikation.
                - Dokumentauszüge können unvollständig sein.
                - Wenn die Daten eine Aussage nicht stützen, sage das.
                - Unterscheide verschiedene Geschäfte klar.
                - Formuliere natürlich und sachlich.
                - Wiederhole nicht unnötig die Benutzerfrage.
                - Behaupte nicht, dass die Resultate alle existierenden
                  parlamentarischen Geschäfte darstellen.
                - Antworte kompakt in höchstens fünf Sätzen.

                Benutzerfrage:
                %s

                Vom Query-Planner erkannte Suchparameter:
                WWF-Thema: %s
                Gebiet: %s
                Zusätzliche Suche: %s
                Sortierung: %s

                Gefundene Daten:
                %s

                Beantworte jetzt die Benutzerfrage.
                """.formatted(
                question,
                query.topic() == null
                        ? "Keine Themenfilterung"
                        : query.topic().getDisplayName(),
                query.locationDisplayName() == null
                        ? "Keine Gebietseinschränkung"
                        : query.locationDisplayName(),
                query.searchText() == null
                        ? "Keine zusätzliche Volltextsuche"
                        : query.searchText(),
                query.sortOrder(),
                context
        );

        long generationStart = System.nanoTime();

        try {

            String answer =
                    ollamaClient.generate(prompt);

            LOG.info(
                    "Chat answer generation completed in {} ms",
                    elapsedMillis(generationStart)
            );

            return new ChatResponse(
                    answer,
                    displayTopic(query.topic()),
                    results
            );

        } catch (RuntimeException e) {

            LOG.warn(
                    "Chat answer generation failed after {} ms: {}",
                    elapsedMillis(generationStart),
                    e.getMessage()
            );

            return new ChatResponse(
                    buildSummaryFallback(query, results),
                    displayTopic(query.topic()),
                    results
            );
        }
    }

    private List<AffairSummaryResponse> search(
            ChatQuery query,
            int limit
    ) {

        return affairSearchRepository.searchForChat(
                topicName(query.topic()),
                query.bodyKey(),
                query.searchText(),
                query.sortOrder(),
                limit,
                0
        );
    }

    private String buildGroundedContext(
            List<AffairSummaryResponse> results,
            String searchText
    ) {

        StringBuilder context =
                new StringBuilder();

        for (AffairSummaryResponse affair : results) {

            long documentStart = System.nanoTime();

            String documentContext =
                    affairSearchRepository
                            .loadRelevantDocumentContext(
                                    affair.id(),
                                    searchText
                            );

            LOG.info(
                    "Document context loaded in {} ms: affairId={}, characters={}",
                    elapsedMillis(documentStart),
                    affair.id(),
                    documentContext.length()
            );

            context.append("GESCHÄFT\n")
                    .append("ID: ")
                    .append(affair.id())
                    .append('\n')
                    .append("Titel: ")
                    .append(affair.title())
                    .append('\n');

            if (affair.topic() != null) {
                context.append("WWF-Thema: ")
                        .append(affair.topic())
                        .append('\n');
            }

            if (affair.type() != null) {
                context.append("Typ: ")
                        .append(affair.type())
                        .append('\n');
            }

            if (affair.state() != null) {
                context.append("Status: ")
                        .append(affair.state())
                        .append('\n');
            }

            if (affair.beginDate() != null) {
                context.append("Datum: ")
                        .append(affair.beginDate())
                        .append('\n');
            }

            if (affair.urlExternal() != null) {
                context.append("Externer Link: ")
                        .append(affair.urlExternal())
                        .append('\n');
            }

            if (!documentContext.isBlank()) {
                context.append("Relevante Dokumentauszüge:\n")
                        .append(documentContext)
                        .append('\n');
            } else {
                context.append(
                        "Dokumentauszüge: Keine Dokumenttexte verfügbar.\n"
                );
            }

            context.append("\n---\n\n");
        }

        return context.toString();
    }

    private ChatResponse noResults(ChatQuery query) {

        StringBuilder answer =
                new StringBuilder(
                        "Es wurden in den aktuell importierten Daten keine passenden parlamentarischen Geschäfte"
                );

        appendDescription(answer, query);
        answer.append(" gefunden.");

        return new ChatResponse(
                answer.toString(),
                displayTopic(query.topic()),
                List.of()
        );
    }

    private void appendDescription(
            StringBuilder answer,
            ChatQuery query
    ) {

        if (query.topic() != null) {
            answer.append(" zum WWF-Thema ")
                    .append(
                            query.topic()
                                    .getDisplayName()
                    );
        }

        if (query.locationDisplayName() != null) {
            answer.append(" im ")
                    .append(
                            query.locationDisplayName()
                    );
        }

        if (query.searchText() != null) {
            answer.append(" zur Suche „")
                    .append(query.searchText())
                    .append("“");
        }
    }

    private String buildSummaryFallback(
            ChatQuery query,
            List<AffairSummaryResponse> results
    ) {

        StringBuilder answer =
                new StringBuilder(
                        "Die automatische Zusammenfassung ist derzeit nicht verfügbar. "
                );

        answer.append("Es wurden ")
                .append(results.size())
                .append(" passende parlamentarische Geschäfte");

        appendDescription(answer, query);
        answer.append(" gefunden.");

        return answer.toString();
    }

    private String topicName(Topic topic) {

        return topic == null
                ? null
                : topic.name();
    }

    private String displayTopic(Topic topic) {

        return topic == null
                ? null
                : topic.getDisplayName();
    }

    private long elapsedMillis(long startNano) {

        return (
                System.nanoTime() - startNano
        ) / 1_000_000;
    }
}