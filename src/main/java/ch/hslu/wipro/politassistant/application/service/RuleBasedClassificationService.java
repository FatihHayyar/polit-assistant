package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.config.ClassificationRulesProperties;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class RuleBasedClassificationService {

    private static final String CLASSIFIER_NAME = "RULE_ENGINE_V2";

    private final SearchDocumentPort searchDocumentPort;
    private final ClassificationStorePort classificationStorePort;
    private final ClassificationRulesProperties properties;

    public RuleBasedClassificationService(
            SearchDocumentPort searchDocumentPort,
            ClassificationStorePort classificationStorePort,
            ClassificationRulesProperties properties
    ) {
        this.searchDocumentPort = searchDocumentPort;
        this.classificationStorePort = classificationStorePort;
        this.properties = properties;
    }

    @Transactional
    public ClassificationResult classify(Long affairId) {

        var document = searchDocumentPort.load(affairId);

        String title = normalize(document.title());
        String titleLong = normalize(document.titleLong());
        String documentContent = normalize(document.documentContent());

        classificationStorePort.deleteByAffairId(affairId);

        List<TopicClassification> classifications = new ArrayList<>();

        for (var entry : properties.getRules().entrySet()) {

            Topic topic = entry.getKey();

            List<String> titleMatches =
                    findMatches(title, entry.getValue());

            List<String> titleLongMatches =
                    findMatches(titleLong, entry.getValue());

            List<String> documentMatches =
                    findMatches(documentContent, entry.getValue());

            boolean hasTitleMatch = !titleMatches.isEmpty();
            boolean hasTitleLongMatch = !titleLongMatches.isEmpty();
            boolean hasEnoughDocumentMatches = documentMatches.size() >= 3;

            if (!hasTitleMatch
                    && !hasTitleLongMatch
                    && !hasEnoughDocumentMatches) {
                continue;
            }

            /*
             * Title is the strongest signal.
             *
             * Title match:
             *      base confidence 0.90
             *
             * Long title match:
             *      base confidence 0.80
             *
             * Document-only match:
             *      base confidence 0.50
             */
            double confidence;

            if (!titleMatches.isEmpty()) {
                confidence = Math.min(
                        1.0,
                        0.90 + (titleMatches.size() - 1) * 0.05
                );
            } else if (!titleLongMatches.isEmpty()) {
                confidence = Math.min(
                        0.90,
                        0.80 + (titleLongMatches.size() - 1) * 0.05
                );
            } else {
                confidence = Math.min(
                        0.80,
                        0.50 + documentMatches.size() * 0.10
                );
            }

            List<String> allMatches = new ArrayList<>();

            titleMatches.forEach(
                    keyword -> allMatches.add("TITLE:" + keyword)
            );

            titleLongMatches.forEach(
                    keyword -> allMatches.add("TITLE_LONG:" + keyword)
            );

            documentMatches.forEach(
                    keyword -> allMatches.add("DOCUMENT:" + keyword)
            );

            classificationStorePort.save(
                    affairId,
                    topic,
                    confidence,
                    CLASSIFIER_NAME,
                    allMatches
            );

            classifications.add(
                    new TopicClassification(
                            topic,
                            confidence,
                            allMatches
                    )
            );
        }

        if (classifications.isEmpty()) {

            classificationStorePort.save(
                    affairId,
                    Topic.OTHER,
                    0.1,
                    CLASSIFIER_NAME,
                    List.of()
            );

            classifications.add(
                    new TopicClassification(
                            Topic.OTHER,
                            0.1,
                            List.of()
                    )
            );
        }

        return new ClassificationResult(
                affairId,
                classifications
        );
    }

    private List<String> findMatches(
            String content,
            List<String> keywords
    ) {
        if (content.isBlank()) {
            return List.of();
        }

        return keywords.stream()
                .map(keyword -> keyword.toLowerCase(Locale.ROOT))
                .filter(keyword -> matchesKeyword(content, keyword))
                .distinct()
                .toList();
    }

    private boolean matchesKeyword(
            String content,
            String keyword
    ) {
        return content.contains(keyword);
    }

    private String normalize(String value) {
        return Optional.ofNullable(value)
                .orElse("")
                .toLowerCase(Locale.ROOT);
    }

    @Transactional
    public int classifyAll(List<Long> affairIds) {

        int count = 0;

        for (Long affairId : affairIds) {
            classify(affairId);
            count++;
        }

        return count;
    }

    public record ClassificationResult(
            Long affairId,
            List<TopicClassification> classifications
    ) {}

    public record TopicClassification(
            Topic topic,
            double confidence,
            List<String> matchedKeywords
    ) {}
}