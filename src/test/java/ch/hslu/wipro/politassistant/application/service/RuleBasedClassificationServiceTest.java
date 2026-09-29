package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.config.ClassificationRulesProperties;
import ch.hslu.wipro.politassistant.domain.classification.AffairSearchDocument;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RuleBasedClassificationServiceTest {

    private SearchDocumentPort searchDocumentPort;
    private ClassificationStorePort classificationStorePort;
    private RuleBasedClassificationService service;

    @BeforeEach
    void setUp() {
        searchDocumentPort = mock(SearchDocumentPort.class);
        classificationStorePort = mock(ClassificationStorePort.class);

        ClassificationRulesProperties properties =
                new ClassificationRulesProperties();

        properties.setRules(Map.of(
                Topic.WATER, List.of(
                        "wasser",
                        "eau",
                        "gewässerschutz"
                ),
                Topic.AGRICULTURE, List.of(
                        "landwirtschaft",
                        "agriculture"
                ),
                Topic.CLIMATE, List.of(
                        "klima",
                        "co2",
                        "klimaschutz"
                ),
                Topic.ENERGY, List.of(
                        "energie",
                        "photovoltaik"
                )
        ));

        service = new RuleBasedClassificationService(
                searchDocumentPort,
                classificationStorePort,
                properties
        );
    }

    @Test
    void shouldClassifyAffairIntoMultipleTopicsFromDifferentSources() {
        Long affairId = 1L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Revision im Bereich Energie",
                        "Förderung der Energieversorgung",
                        "Massnahmen für Klimaschutz und Gewässerschutz"
                ));

        var result = service.classify(affairId);

        assertEquals(3, result.classifications().size());

        var energy = findClassification(result, Topic.ENERGY);
        var climate = findClassification(result, Topic.CLIMATE);
        var water = findClassification(result, Topic.WATER);

        assertNotNull(energy);
        assertNotNull(climate);
        assertNotNull(water);

        assertTrue(
                energy.matchedKeywords().contains("TITLE:energie")
        );

        assertTrue(
                climate.matchedKeywords().contains("DOCUMENT:klimaschutz")
        );

        assertTrue(
                water.matchedKeywords().contains("DOCUMENT:gewässerschutz")
        );

        verify(classificationStorePort)
                .deleteByAffairId(affairId);

        verify(classificationStorePort, times(3))
                .save(
                        eq(affairId),
                        any(Topic.class),
                        anyDouble(),
                        eq("RULE_ENGINE_V2"),
                        anyList()
                );
    }


    @Test
    void shouldUseLongTitleAsStrongSignal() {
        Long affairId = 3L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Änderung eines Gesetzes",
                        "Massnahmen zur Förderung der Landwirtschaft",
                        ""
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var agriculture = result.classifications().getFirst();

        assertEquals(Topic.AGRICULTURE, agriculture.topic());
        assertEquals(0.80, agriculture.confidence(), 0.0001);

        assertTrue(
                agriculture.matchedKeywords()
                        .contains("TITLE_LONG:landwirtschaft")
        );
    }

    @Test
    void shouldClassifyGeneralTitleUsingDocumentContent() {
        Long affairId = 4L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Bericht des Regierungsrates",
                        "",
                        "Der Bericht behandelt Klima, CO2 und Klimaschutz."
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var climate = result.classifications().getFirst();

        assertEquals(Topic.CLIMATE, climate.topic());

        /*
         * 3 document keyword matches:
         * 0.50 + 3 * 0.10 = 0.80
         */
        assertEquals(0.80, climate.confidence(), 0.0001);

        assertTrue(
                climate.matchedKeywords().contains("DOCUMENT:klima")
        );
        assertTrue(
                climate.matchedKeywords().contains("DOCUMENT:co2")
        );
        assertTrue(
                climate.matchedKeywords().contains("DOCUMENT:klimaschutz")
        );
    }

    @Test
    void shouldStoreOtherWhenNoTopicMatches() {
        Long affairId = 5L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Wahl eines Mitglieds der Geschäftsprüfungskommission",
                        "",
                        "Bericht über die Wahl eines neuen Mitglieds."
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var classification = result.classifications().getFirst();

        assertEquals(Topic.OTHER, classification.topic());
        assertEquals(0.1, classification.confidence(), 0.0001);
        assertTrue(classification.matchedKeywords().isEmpty());

        verify(classificationStorePort)
                .save(
                        eq(affairId),
                        eq(Topic.OTHER),
                        eq(0.1),
                        eq("RULE_ENGINE_V2"),
                        eq(List.of())
                );
    }


    @Test
    void shouldPreferTitleConfidenceEvenWhenTopicAlsoAppearsInDocument() {
        Long affairId = 7L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Energie und Photovoltaik",
                        "",
                        "Der Bericht enthält weitere Informationen zur Energie."
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var energy = result.classifications().getFirst();

        assertEquals(Topic.ENERGY, energy.topic());

        /*
         * Two title matches:
         * 0.90 + 0.05 = 0.95
         *
         * Document match must not lower the title-based confidence.
         */
        assertEquals(0.95, energy.confidence(), 0.0001);

        assertTrue(
                energy.matchedKeywords().contains("TITLE:energie")
        );
        assertTrue(
                energy.matchedKeywords().contains("TITLE:photovoltaik")
        );
        assertTrue(
                energy.matchedKeywords().contains("DOCUMENT:energie")
        );
    }

    private RuleBasedClassificationService.TopicClassification findClassification(
            RuleBasedClassificationService.ClassificationResult result,
            Topic topic
    ) {
        return result.classifications().stream()
                .filter(classification -> classification.topic() == topic)
                .findFirst()
                .orElse(null);
    }
}