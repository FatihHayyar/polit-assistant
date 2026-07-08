package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RuleBasedClassificationService {

    private static final String CLASSIFIER_NAME = "RULE_ENGINE_V1";

    private final SearchDocumentPort searchDocumentPort;
    private final ClassificationStorePort classificationStorePort;

    private final Map<Topic, List<String>> keywords = Map.of(
            Topic.ENERGY, List.of("energie", "strom", "elektrizität", "solar", "photovoltaik", "wind", "wasserkraft"),
            Topic.BIODIVERSITY, List.of("biodiversität", "artenvielfalt", "schutzgebiet", "ökosystem", "wald", "insekten"),
            Topic.WATER, List.of("wasser", "gewässer", "fluss", "see", "abwasser", "renaturierung"),
            Topic.AGRICULTURE, List.of("landwirtschaft", "pestizid", "boden", "tierhaltung", "dünger"),
            Topic.SPATIAL_PLANNING, List.of("raumplanung", "bauzone", "verdichtung", "siedlung", "landschaftsschutz"),
            Topic.CLIMATE, List.of("klima", "co2", "treibhausgas", "emission", "klimaschutz"),
            Topic.MOBILITY, List.of("verkehr", "mobilität", "öffentlicher verkehr", "öV", "bahn", "veloverkehr"),
            Topic.WASTE, List.of("abfall", "recycling", "plastik", "kreislaufwirtschaft")
    );

    public RuleBasedClassificationService(
            SearchDocumentPort searchDocumentPort,
            ClassificationStorePort classificationStorePort
    ) {
        this.searchDocumentPort = searchDocumentPort;
        this.classificationStorePort = classificationStorePort;
    }

    @Transactional
    public ClassificationResult classify(Long affairId) {
        var document = searchDocumentPort.load(affairId);

        String content = Optional.ofNullable(document.content())
                .orElse("")
                .toLowerCase(Locale.ROOT);

        Topic bestTopic = Topic.OTHER;
        List<String> bestMatches = List.of();

        for (var entry : keywords.entrySet()) {
            List<String> matches = entry.getValue().stream()
                    .filter(content::contains)
                    .toList();

            if (matches.size() > bestMatches.size()) {
                bestTopic = entry.getKey();
                bestMatches = matches;
            }
        }

        double confidence = bestTopic == Topic.OTHER ? 0.1 : Math.min(1.0, 0.5 + bestMatches.size() * 0.1);
        classificationStorePort.deleteByAffairId(affairId);
        classificationStorePort.save(
                affairId,
                bestTopic,
                confidence,
                CLASSIFIER_NAME,
                bestMatches
        );

        return new ClassificationResult(affairId, bestTopic, confidence, bestMatches);
    }

    public record ClassificationResult(
            Long affairId,
            Topic topic,
            double confidence,
            List<String> matchedKeywords
    ) {}
    @Transactional
    public int classifyAll(List<Long> affairIds) {
        int count = 0;

        for (Long affairId : affairIds) {
            classify(affairId);
            count++;
        }

        return count;
    }
}