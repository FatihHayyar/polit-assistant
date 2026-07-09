package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.adapter.in.rest.dto.ChatResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairSearchJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final AffairSearchJdbcRepository searchRepository;
    private final ChatIntentParser intentParser;

    public ChatService(
            AffairSearchJdbcRepository searchRepository,
            ChatIntentParser intentParser
    ) {
        this.searchRepository = searchRepository;
        this.intentParser = intentParser;
    }

    public ChatResponse ask(String question) {
        var topic = intentParser.detectTopic(question);

        List<AffairSummaryResponse> results = topic
                .map(value -> searchRepository.filterByTopic(value, 20, 0))
                .orElseGet(() -> searchRepository.fullTextSearch(question, null, 20, 0));

        String answer;

        if (results.isEmpty()) {
            answer = "I couldn't find any matching political affairs.";
        } else if (results.size() == 1) {
            answer = "I found one matching political affair.";
        } else {
            answer = "I found %d matching political affairs. Here are the most relevant results."
                    .formatted(results.size());
        }

        return new ChatResponse(answer, topic.orElse(null),results);
    }
}