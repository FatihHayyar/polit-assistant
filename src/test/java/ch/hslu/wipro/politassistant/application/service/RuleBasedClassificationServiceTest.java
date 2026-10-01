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

    private static final String CLASSIFIER_NAME = "RULE_ENGINE_V3";

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
                Topic.WASSER, List.of(
                        "wasser",
                        "eau",
                        "eaux",
                        "gewässer",
                        "gewässerschutz",
                        "grundwasser",
                        "abwasser"
                ),
                Topic.LANDWIRTSCHAFT, List.of(
                        "landwirtschaft",
                        "agriculture",
                        "pestizid"
                ),
                Topic.KLIMA, List.of(
                        "klima",
                        "co2",
                        "klimaschutz"
                ),
                Topic.ENERGIE, List.of(
                        "energie",
                        "photovoltaik",
                        "strom"
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
                        "Revision im Bereich Energie und Landwirtschaft",
                        "Förderung der Energieversorgung",
                        List.of(
                                "Massnahmen für Klima, CO2 und Klimaschutz."
                        )
                ));

        var result = service.classify(affairId);

        assertEquals(3, result.classifications().size());

        var energie =
                findClassification(result, Topic.ENERGIE);

        var landwirtschaft =
                findClassification(result, Topic.LANDWIRTSCHAFT);

        var klima =
                findClassification(result, Topic.KLIMA);

        assertNotNull(energie);
        assertNotNull(landwirtschaft);
        assertNotNull(klima);

        assertTrue(
                energie.matchedKeywords()
                        .contains("TITLE:energie")
        );

        assertTrue(
                landwirtschaft.matchedKeywords()
                        .contains("TITLE:landwirtschaft")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[1]:klima")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[1]:co2")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[1]:klimaschutz")
        );

        verify(classificationStorePort)
                .deleteByAffairId(affairId);

        verify(classificationStorePort, times(3))
                .save(
                        eq(affairId),
                        any(Topic.class),
                        anyDouble(),
                        eq(CLASSIFIER_NAME),
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
                        List.of()
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var landwirtschaft =
                result.classifications().getFirst();

        assertEquals(
                Topic.LANDWIRTSCHAFT,
                landwirtschaft.topic()
        );

        assertEquals(
                0.80,
                landwirtschaft.confidence(),
                0.0001
        );

        assertTrue(
                landwirtschaft.matchedKeywords()
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
                        List.of(
                                "Der Bericht behandelt Klima, CO2 und Klimaschutz."
                        )
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var klima =
                result.classifications().getFirst();

        assertEquals(
                Topic.KLIMA,
                klima.topic()
        );

        /*
         * Drei unterschiedliche Dokument-Keywords
         * innerhalb desselben lokalen Fensters:
         *
         * 0.50 + 3 * 0.10 = 0.80
         */
        assertEquals(
                0.80,
                klima.confidence(),
                0.0001
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[1]:klima")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[1]:co2")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[1]:klimaschutz")
        );
    }

    @Test
    void shouldNotCombineEvidenceAcrossDifferentDocuments() {

        Long affairId = 5L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Allgemeiner Bericht",
                        "",
                        List.of(
                                "Dieses Dokument erwähnt Klima.",
                                "Dieses Dokument erwähnt CO2.",
                                "Dieses Dokument erwähnt Klimaschutz."
                        )
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var classification =
                result.classifications().getFirst();

        assertEquals(
                Topic.SONSTIGES,
                classification.topic()
        );

        assertEquals(
                0.1,
                classification.confidence(),
                0.0001
        );
    }

    @Test
    void shouldStoreSonstigesWhenNoTopicMatches() {

        Long affairId = 6L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Wahl eines Mitglieds der Geschäftsprüfungskommission",
                        "",
                        List.of(
                                "Bericht über die Wahl eines neuen Mitglieds."
                        )
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var classification =
                result.classifications().getFirst();

        assertEquals(
                Topic.SONSTIGES,
                classification.topic()
        );

        assertEquals(
                0.1,
                classification.confidence(),
                0.0001
        );

        assertTrue(
                classification.matchedKeywords().isEmpty()
        );

        verify(classificationStorePort)
                .save(
                        eq(affairId),
                        eq(Topic.SONSTIGES),
                        eq(0.1),
                        eq(CLASSIFIER_NAME),
                        eq(List.of())
                );
    }

    @Test
    void shouldPreferTitleConfidenceWhenDocumentEvidenceIsInsufficient() {

        Long affairId = 7L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Energie und Photovoltaik",
                        "",
                        List.of(
                                "Der Bericht enthält weitere Informationen zur Energie."
                        )
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var energie = result.classifications().getFirst();

        assertEquals(
                Topic.ENERGIE,
                energie.topic()
        );

        /*
         * Zwei Treffer im Titel:
         * 0.90 + 0.05 = 0.95.
         *
         * Der einzelne Dokumenttreffer reicht nicht
         * für die minimale Dokument-Evidenz von drei
         * unabhängigen Treffern aus.
         */
        assertEquals(
                0.95,
                energie.confidence(),
                0.0001
        );

        assertTrue(
                energie.matchedKeywords()
                        .contains("TITLE:energie")
        );

        assertTrue(
                energie.matchedKeywords()
                        .contains("TITLE:photovoltaik")
        );

        assertFalse(
                energie.matchedKeywords()
                        .contains("DOCUMENT[1]:energie")
        );
    }
    @Test
    void shouldKeepDocumentNumbersSeparate() {

        Long affairId = 8L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Allgemeiner Bericht",
                        "",
                        List.of(
                                "Dieses Dokument enthält keine relevante Aussage.",
                                "Klima, CO2 und Klimaschutz werden gemeinsam behandelt."
                        )
                ));

        var result = service.classify(affairId);

        var klima =
                findClassification(result, Topic.KLIMA);

        assertNotNull(klima);

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[2]:klima")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[2]:co2")
        );

        assertTrue(
                klima.matchedKeywords()
                        .contains("DOCUMENT[2]:klimaschutz")
        );
    }

    @Test
    void shouldNotMatchShortKeywordInsideAnotherWord() {

        Long affairId = 9L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Introduction d'un nouveau mode de subventionnement",
                        "",
                        List.of()
                ));

        var result = service.classify(affairId);

        assertEquals(1, result.classifications().size());

        var classification =
                result.classifications().getFirst();

        assertEquals(
                Topic.SONSTIGES,
                classification.topic()
        );

        assertFalse(
                classification.matchedKeywords()
                        .contains("TITLE:eau")
        );
    }

    @Test
    void shouldStillMatchLongKeywordInsideGermanCompound() {

        Long affairId = 10L;

        when(searchDocumentPort.load(affairId))
                .thenReturn(new AffairSearchDocument(
                        affairId,
                        "Massnahmen zum Hochwasserschutz",
                        "",
                        List.of()
                ));

        var result = service.classify(affairId);

        var wasser =
                findClassification(result, Topic.WASSER);

        assertNotNull(wasser);

        assertTrue(
                wasser.matchedKeywords()
                        .contains("TITLE:wasser")
        );
    }

    private RuleBasedClassificationService.TopicClassification findClassification(
            RuleBasedClassificationService.ClassificationResult result,
            Topic topic
    ) {
        return result.classifications()
                .stream()
                .filter(classification ->
                        classification.topic() == topic
                )
                .findFirst()
                .orElse(null);
    }
}