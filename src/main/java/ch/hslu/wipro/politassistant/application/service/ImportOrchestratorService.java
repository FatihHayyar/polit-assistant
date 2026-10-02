package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.domain.alert.AlertEvent;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ImportOrchestratorService {

    private static final Logger log =
            LoggerFactory.getLogger(ImportOrchestratorService.class);

    private final AffairImportService affairImportService;
    private final RuleBasedClassificationService classificationService;
    private final RelevantAffairDetectionService relevantAffairDetectionService;
    private final AlertService alertService;
    private final SyncStateService syncStateService;
    private final MeetingAgendaImportService meetingAgendaImportService;
    private final ClassificationStorePort classificationStorePort;

    public ImportOrchestratorService(
            AffairImportService affairImportService,
            RuleBasedClassificationService classificationService,
            RelevantAffairDetectionService relevantAffairDetectionService,
            AlertService alertService,
            SyncStateService syncStateService,
            MeetingAgendaImportService meetingAgendaImportService,
            ClassificationStorePort classificationStorePort
    ) {
        this.affairImportService = affairImportService;
        this.classificationService = classificationService;
        this.relevantAffairDetectionService = relevantAffairDetectionService;
        this.alertService = alertService;
        this.syncStateService = syncStateService;
        this.meetingAgendaImportService = meetingAgendaImportService;
        this.classificationStorePort = classificationStorePort;
    }

    public InitialAffairImportResult runInitialAffairImport(
            int limit
    ) {
        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "limit must be > 0"
            );
        }

        Integer savedOffset =
                syncStateService.getLastOffset(
                        SyncStateService.OPENPARLDATA_INITIAL_AFFAIRS
                );

        int currentOffset =
                savedOffset != null
                        ? savedOffset
                        : 0;

        int importedThisRun = 0;
        int pagesProcessed = 0;

        log.info(
                "Initial affair import started/resumed: offset={}, pageSize={}",
                currentOffset,
                limit
        );

        while (true) {
            int pageOffset = currentOffset;

            log.info(
                    "Initial affair import: fetching offset={}, limit={}",
                    pageOffset,
                    limit
            );

            int imported =
                    affairImportService.importAffairs(
                            pageOffset,
                            limit
                    );

            if (imported == 0) {
                log.info(
                        "Initial affair import: no more records at offset={}",
                        pageOffset
                );
                break;
            }

            importedThisRun += imported;
            pagesProcessed++;
            currentOffset = pageOffset + imported;

            syncStateService.updateLastOffset(
                    SyncStateService.OPENPARLDATA_INITIAL_AFFAIRS,
                    currentOffset
            );

            log.info(
                    "Initial affair import progress: nextOffset={}, importedThisPage={}, importedThisRun={}, pagesProcessed={}",
                    currentOffset,
                    imported,
                    importedThisRun,
                    pagesProcessed
            );

            if (imported < limit) {
                log.info(
                        "Initial affair import: final page reached at offset={}, returned={}",
                        pageOffset,
                        imported
                );
                break;
            }
        }

        log.info(
                "Initial affair import completed: finalOffset={}, importedThisRun={}, pagesProcessed={}",
                currentOffset,
                importedThisRun,
                pagesProcessed
        );

        return new InitialAffairImportResult(
                currentOffset,
                importedThisRun,
                pagesProcessed
        );
    }

    public FullImportResult runFullImport(
            int offset,
            int limit
    ) {
        if (offset < 0) {
            throw new IllegalArgumentException(
                    "offset must be >= 0"
            );
        }

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "limit must be > 0"
            );
        }

        int currentOffset = offset;
        int affairsImported = 0;
        int docsImported = 0;
        int classified = 0;
        int relevantAffairs = 0;

        LocalDateTime maxUpdatedAt = null;

        log.info(
                "Legacy full import started: offset={}, pageSize={}",
                offset,
                limit
        );

        while (true) {
            var result =
                    affairImportService.importAffairsWithDocsOnly(
                            currentOffset,
                            limit
                    );

            if (result.affairsImported() == 0) {
                break;
            }

            int pageClassified =
                    classificationService.classifyAll(
                            result.importedAffairIds()
                    );

            int pageRelevant =
                    relevantAffairDetectionService.countRelevantAffairs(
                            result.importedAffairIds()
                    );

            affairsImported += result.affairsImported();
            docsImported += result.docsImported();
            classified += pageClassified;
            relevantAffairs += pageRelevant;

            if (result.maxUpdatedAt() != null
                    && (maxUpdatedAt == null
                    || result.maxUpdatedAt().isAfter(maxUpdatedAt))) {
                maxUpdatedAt = result.maxUpdatedAt();
            }

            if (result.affairsImported() < limit) {
                break;
            }

            currentOffset += limit;
        }

        if (maxUpdatedAt != null) {
            syncStateService.updateLastSuccessfulSync(
                    SyncStateService.OPENPARLDATA_AFFAIRS,
                    maxUpdatedAt
            );
        }

        /*
         * Historical/full baseline imports never create alerts.
         */
        return new FullImportResult(
                affairsImported,
                docsImported,
                classified,
                relevantAffairs,
                0
        );
    }

    @Transactional
    public FullImportResult runIncrementalImport(
            int limit
    ) {
        var lastSync =
                syncStateService.getLastSuccessfulSync(
                        SyncStateService.OPENPARLDATA_AFFAIRS
                );

        log.info(
                "Incremental affair import started: lastSync={}, pageSize={}",
                lastSync,
                limit
        );

        var result =
                affairImportService.importLatestAffairsOnly(
                        limit,
                        lastSync
                );

        if (result.importedAffairIds().isEmpty()) {
            log.info("Incremental affair import: no changed affairs found.");

            return new FullImportResult(
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }

        Map<Long, Set<Topic>> topicsBefore =
                classificationStorePort.findTopicsByAffairIds(
                        result.importedAffairIds()
                );

        int classified =
                classificationService.classifyAll(
                        result.importedAffairIds()
                );

        Map<Long, Set<Topic>> topicsAfter =
                classificationStorePort.findTopicsByAffairIds(
                        result.importedAffairIds()
                );

        int relevantAffairs =
                relevantAffairDetectionService.countRelevantAffairs(
                        result.importedAffairIds()
                );

        List<AlertEvent> events =
                buildIncrementalEvents(
                        result,
                        topicsBefore,
                        topicsAfter
                );

        int alertsCreated =
                alertService.createAlertsForEvents(events);

        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(
                    SyncStateService.OPENPARLDATA_AFFAIRS,
                    result.maxUpdatedAt()
            );
        }

        log.info(
                "Incremental affair import completed: affairs={}, docs={}, classified={}, relevantAffairs={}, events={}, alertsCreated={}",
                result.affairsImported(),
                result.docsImported(),
                classified,
                relevantAffairs,
                events.size(),
                alertsCreated
        );

        return new FullImportResult(
                result.affairsImported(),
                result.docsImported(),
                classified,
                relevantAffairs,
                alertsCreated
        );
    }

    private List<AlertEvent> buildIncrementalEvents(
            ImportResult result,
            Map<Long, Set<Topic>> topicsBefore,
            Map<Long, Set<Topic>> topicsAfter
    ) {
        List<AlertEvent> events = new ArrayList<>();

        Set<Long> newAffairIds =
                new HashSet<>(
                        result.newAffairIds()
                );

        /*
         * A new affair produces one AFFAIR_NEW event.
         * Its initial documents and initial classifications do not produce
         * additional notifications.
         */
        for (Long affairId : newAffairIds) {
            if (hasRelevantTopic(
                    topicsAfter.get(affairId)
            )) {
                events.add(
                        AlertEvent.newAffair(
                                affairId
                        )
                );
            }
        }

        /*
         * DOCUMENT_NEW events are already restricted by AffairImportService
         * to documents added to affairs that existed before this update.
         *
         * AlertService will only create notifications if the affair currently
         * has a relevant WWF classification.
         */
        events.addAll(
                result.newDocumentEvents()
        );

        /*
         * Existing affairs can gain a new WWF topic after new/changed data
         * causes reclassification.
         */
        for (Long affairId : result.importedAffairIds()) {
            if (newAffairIds.contains(affairId)) {
                continue;
            }

            Set<Topic> before =
                    topicsBefore.getOrDefault(
                            affairId,
                            Set.of()
                    );

            Set<Topic> after =
                    topicsAfter.getOrDefault(
                            affairId,
                            Set.of()
                    );

            for (Topic topic : after) {
                if (topic == Topic.SONSTIGES) {
                    continue;
                }

                if (!before.contains(topic)) {
                    events.add(
                            AlertEvent.newTopic(
                                    affairId,
                                    topic.name()
                            )
                    );
                }
            }
        }

        return events;
    }

    private boolean hasRelevantTopic(
            Set<Topic> topics
    ) {
        if (topics == null || topics.isEmpty()) {
            return false;
        }

        return topics.stream()
                .anyMatch(
                        topic -> topic != Topic.SONSTIGES
                );
    }

    @Transactional
    public MeetingUpdateResult runMeetingAgendaImport(
            int limit
    ) {
        var lastSync =
                syncStateService.getLastSuccessfulSync(
                        SyncStateService.OPENPARLDATA_MEETINGS
                );

        log.info(
                "Incremental meeting/agenda import started: lastSync={}, pageSize={}",
                lastSync,
                limit
        );

        var result =
                meetingAgendaImportService.importMeetingsWithAgendas(
                        0,
                        limit,
                        lastSync
                );

        List<Long> missingAffairIds =
                result.importedMissingAffairIds()
                        .stream()
                        .distinct()
                        .toList();

        if (!missingAffairIds.isEmpty()) {
            classificationService.classifyAll(
                    missingAffairIds
            );

            log.info(
                    "Classified {} affairs discovered through agenda import.",
                    missingAffairIds.size()
            );
        }

        int alertsCreated =
                alertService.createAlertsForEvents(
                        result.events()
                );

        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(
                    SyncStateService.OPENPARLDATA_MEETINGS,
                    result.maxUpdatedAt()
            );
        }

        log.info(
                "Incremental meeting/agenda import completed: meetings={}, agendas={}, events={}, alertsCreated={}",
                result.importedMeetings(),
                result.importedAgendas(),
                result.events().size(),
                alertsCreated
        );

        return new MeetingUpdateResult(
                result.importedMeetings(),
                result.importedAgendas(),
                result.events().size(),
                alertsCreated
        );
    }
    public record InitialAffairImportResult(
            int finalOffset,
            int importedThisRun,
            int pagesProcessed
    ) {
    }
    public record MeetingUpdateResult(
            int importedMeetings,
            int importedAgendas,
            int eventsDetected,
            int alertsCreated
    ) {
    }
    public record FullImportResult(
            int affairsImported,
            int docsImported,
            int classified,
            int relevantAffairs,
            int alertsCreated
    ) {
    }

    public record ImportResult(
            int affairsImported,
            int docsImported,
            ArrayList<Long> importedAffairIds,
            ArrayList<Long> newAffairIds,
            ArrayList<AlertEvent> newDocumentEvents,
            LocalDateTime maxUpdatedAt
    ) {
    }
}