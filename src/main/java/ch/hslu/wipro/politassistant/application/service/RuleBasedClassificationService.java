package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.config.ClassificationRulesProperties;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class RuleBasedClassificationService {

    private static final String CLASSIFIER_NAME = "RULE_ENGINE_V3";

    private static final int DOCUMENT_WINDOW_SIZE = 800;
    private static final int MIN_DOCUMENT_EVIDENCE = 3;

    /*
     * Very short keywords are prone to accidental substring matches.
     *
     * Examples:
     *   "eau" in "nouveau" must NOT match.
     *   "öv" inside another word must NOT match.
     *
     * Longer keywords still use substring matching so that German
     * compounds such as "Hochwasserschutz" or "Fliessgewässern"
     * continue to work.
     */
    private static final int WHOLE_WORD_MAX_LENGTH = 4;

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

        classificationStorePort.deleteByAffairId(affairId);

        List<TopicClassification> classifications = new ArrayList<>();

        for (var entry : properties.getRules().entrySet()) {

            Topic topic = entry.getKey();
            List<String> keywords = normalizeKeywords(entry.getValue());

            List<String> titleMatches =
                    findKeywordMatches(title, keywords);

            List<String> titleLongMatches =
                    findKeywordMatches(titleLong, keywords);

            DocumentEvidence documentEvidence =
                    findBestDocumentEvidence(
                            document.documentContents(),
                            keywords
                    );

            boolean hasTitleMatch = !titleMatches.isEmpty();
            boolean hasTitleLongMatch = !titleLongMatches.isEmpty();

            boolean hasDocumentEvidence =
                    documentEvidence != null
                            && documentEvidence.keywords().size()
                            >= MIN_DOCUMENT_EVIDENCE;

            if (!hasTitleMatch
                    && !hasTitleLongMatch
                    && !hasDocumentEvidence) {
                continue;
            }

            double confidence;

            if (hasTitleMatch) {

                confidence = Math.min(
                        1.0,
                        0.90 + (titleMatches.size() - 1) * 0.05
                );

            } else if (hasTitleLongMatch) {

                confidence = Math.min(
                        0.90,
                        0.80 + (titleLongMatches.size() - 1) * 0.05
                );

            } else {

                confidence = Math.min(
                        0.80,
                        0.50
                                + documentEvidence.keywords().size() * 0.10
                );
            }

            List<String> allMatches = new ArrayList<>();

            titleMatches.forEach(
                    keyword ->
                            allMatches.add("TITLE:" + keyword)
            );

            titleLongMatches.forEach(
                    keyword ->
                            allMatches.add("TITLE_LONG:" + keyword)
            );

            if (hasDocumentEvidence) {

                documentEvidence.keywords().forEach(
                        keyword ->
                                allMatches.add(
                                        "DOCUMENT["
                                                + documentEvidence.documentNumber()
                                                + "]:"
                                                + keyword
                                )
                );
            }

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
                    Topic.SONSTIGES,
                    0.1,
                    CLASSIFIER_NAME,
                    List.of()
            );

            classifications.add(
                    new TopicClassification(
                            Topic.SONSTIGES,
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

    private DocumentEvidence findBestDocumentEvidence(
            List<String> documentContents,
            List<String> keywords
    ) {

        DocumentEvidence bestEvidence = null;

        for (int documentIndex = 0;
             documentIndex < documentContents.size();
             documentIndex++) {

            String content =
                    normalize(documentContents.get(documentIndex));

            if (content.isBlank()) {
                continue;
            }

            List<Occurrence> occurrences =
                    findOccurrences(content, keywords);

            if (occurrences.size() < MIN_DOCUMENT_EVIDENCE) {
                continue;
            }

            DocumentEvidence evidence =
                    findBestWindow(
                            occurrences,
                            documentIndex + 1
                    );

            if (evidence == null) {
                continue;
            }

            if (bestEvidence == null
                    || evidence.keywords().size()
                    > bestEvidence.keywords().size()) {

                bestEvidence = evidence;
            }
        }

        return bestEvidence;
    }

    private DocumentEvidence findBestWindow(
            List<Occurrence> occurrences,
            int documentNumber
    ) {

        DocumentEvidence best = null;

        for (int start = 0; start < occurrences.size(); start++) {

            int windowStart = occurrences.get(start).start();
            int windowEnd = windowStart + DOCUMENT_WINDOW_SIZE;

            List<Occurrence> windowOccurrences =
                    occurrences.stream()
                            .filter(occurrence ->
                                    occurrence.start() >= windowStart
                                            && occurrence.start() <= windowEnd
                            )
                            .toList();

            List<Occurrence> independentOccurrences =
                    removeOverlappingOccurrences(windowOccurrences);

            List<String> distinctKeywords =
                    independentOccurrences.stream()
                            .map(Occurrence::keyword)
                            .distinct()
                            .toList();

            if (distinctKeywords.size() < MIN_DOCUMENT_EVIDENCE) {
                continue;
            }

            DocumentEvidence candidate =
                    new DocumentEvidence(
                            documentNumber,
                            distinctKeywords
                    );

            if (best == null
                    || candidate.keywords().size()
                    > best.keywords().size()) {

                best = candidate;
            }
        }

        return best;
    }

    private List<Occurrence> findOccurrences(
            String content,
            List<String> keywords
    ) {

        List<Occurrence> occurrences = new ArrayList<>();

        /*
         * Longer keywords first so that expressions such as
         * "eaux usées" are preferred over contained shorter terms.
         */
        List<String> sortedKeywords =
                keywords.stream()
                        .sorted(
                                Comparator.comparingInt(String::length)
                                        .reversed()
                        )
                        .toList();

        for (String keyword : sortedKeywords) {

            int fromIndex = 0;

            while (fromIndex < content.length()) {

                int index = content.indexOf(keyword, fromIndex);

                if (index < 0) {
                    break;
                }

                int end = index + keyword.length();

                if (isValidOccurrence(
                        content,
                        keyword,
                        index,
                        end
                )) {

                    occurrences.add(
                            new Occurrence(
                                    keyword,
                                    index,
                                    end
                            )
                    );
                }

                fromIndex = index + 1;
            }
        }

        return occurrences.stream()
                .sorted(Comparator.comparingInt(Occurrence::start))
                .toList();
    }

    private List<Occurrence> removeOverlappingOccurrences(
            List<Occurrence> occurrences
    ) {

        List<Occurrence> sorted =
                occurrences.stream()
                        .sorted(
                                Comparator
                                        .comparingInt(Occurrence::start)
                                        .thenComparing(
                                                Comparator.comparingInt(
                                                        Occurrence::length
                                                ).reversed()
                                        )
                        )
                        .toList();

        List<Occurrence> accepted = new ArrayList<>();

        for (Occurrence candidate : sorted) {

            boolean overlaps = accepted.stream()
                    .anyMatch(existing ->
                            candidate.start() < existing.end()
                                    && candidate.end() > existing.start()
                    );

            if (!overlaps) {
                accepted.add(candidate);
            }
        }

        return accepted;
    }

    private List<String> findKeywordMatches(
            String content,
            List<String> keywords
    ) {

        if (content.isBlank()) {
            return List.of();
        }

        return keywords.stream()
                .filter(keyword ->
                        containsValidKeyword(content, keyword)
                )
                .distinct()
                .toList();
    }

    private boolean containsValidKeyword(
            String content,
            String keyword
    ) {

        int fromIndex = 0;

        while (fromIndex < content.length()) {

            int index = content.indexOf(keyword, fromIndex);

            if (index < 0) {
                return false;
            }

            int end = index + keyword.length();

            if (isValidOccurrence(
                    content,
                    keyword,
                    index,
                    end
            )) {
                return true;
            }

            fromIndex = index + 1;
        }

        return false;
    }

    private boolean isValidOccurrence(
            String content,
            String keyword,
            int start,
            int end
    ) {

        /*
         * Longer keywords intentionally support substring matching.
         * This is necessary for German compound words:
         *
         * wasser   -> Hochwasserschutz
         * gewässer -> Fliessgewässern
         */
        if (keyword.length() > WHOLE_WORD_MAX_LENGTH) {
            return true;
        }

        /*
         * Short keywords must be independent words.
         *
         * eau -> "ressource en eau"  : valid
         * eau -> "nouveau"           : invalid
         */
        boolean validStart =
                start == 0
                        || !Character.isLetterOrDigit(
                        content.charAt(start - 1)
                );

        boolean validEnd =
                end >= content.length()
                        || !Character.isLetterOrDigit(
                        content.charAt(end)
                );

        return validStart && validEnd;
    }

    private List<String> normalizeKeywords(
            List<String> keywords
    ) {

        return keywords.stream()
                .filter(keyword ->
                        keyword != null && !keyword.isBlank()
                )
                .map(keyword ->
                        keyword.toLowerCase(Locale.ROOT).trim()
                )
                .distinct()
                .toList();
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
    ) {
    }

    public record TopicClassification(
            Topic topic,
            double confidence,
            List<String> matchedKeywords
    ) {
    }

    private record Occurrence(
            String keyword,
            int start,
            int end
    ) {

        int length() {
            return end - start;
        }
    }

    private record DocumentEvidence(
            int documentNumber,
            List<String> keywords
    ) {
    }
}