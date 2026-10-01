package ch.hslu.wipro.politassistant.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ImportOrchestratorService {

    private final AffairImportService affairImportService;
    private final RuleBasedClassificationService classificationService;
    private final RelevantAffairDetectionService relevantAffairDetectionService;
    private final AlertService alertService;
    private final SyncStateService syncStateService;
    private final MeetingAgendaImportService meetingAgendaImportService;

    public ImportOrchestratorService(
            AffairImportService affairImportService,
            RuleBasedClassificationService classificationService,
            RelevantAffairDetectionService relevantAffairDetectionService,
            AlertService alertService,
            SyncStateService syncStateService,
            MeetingAgendaImportService meetingAgendaImportService
    ) {
        this.affairImportService = affairImportService;
        this.classificationService = classificationService;
        this.relevantAffairDetectionService = relevantAffairDetectionService;
        this.alertService = alertService;
        this.syncStateService = syncStateService;
        this.meetingAgendaImportService = meetingAgendaImportService;
    }

    @Transactional
    public FullImportResult runFullImport(int offset, int limit) {
        var result = affairImportService.importAffairsWithDocsOnly(offset, limit);

        int classified =
                classificationService.classifyAll(result.importedAffairIds());

        int relevantAffairs =
                relevantAffairDetectionService.countRelevantAffairs(
                        result.importedAffairIds()
                );

        int alertsCreated =
                alertService.createAlertsForImportedAffairs(
                        result.importedAffairIds()
                );

        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(
                    SyncStateService.OPENPARLDATA_AFFAIRS,
                    result.maxUpdatedAt()
            );
        }

        return new FullImportResult(
                result.affairsImported(),
                result.docsImported(),
                classified,
                relevantAffairs,
                alertsCreated
        );
    }

    @Transactional
    public FullImportResult runIncrementalImport(int limit) {
        var lastSync = syncStateService.getLastSuccessfulSync(
                SyncStateService.OPENPARLDATA_AFFAIRS
        );

        var result =
                affairImportService.importLatestAffairsOnly(limit, lastSync);

        int classified =
                classificationService.classifyAll(result.importedAffairIds());

        int relevantAffairs =
                relevantAffairDetectionService.countRelevantAffairs(
                        result.importedAffairIds()
                );

        int alertsCreated =
                alertService.createAlertsForImportedAffairs(
                        result.importedAffairIds()
                );

        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(
                    SyncStateService.OPENPARLDATA_AFFAIRS,
                    result.maxUpdatedAt()
            );
        }

        return new FullImportResult(
                result.affairsImported(),
                result.docsImported(),
                classified,
                relevantAffairs,
                alertsCreated
        );
    }

    @Transactional
    public MeetingAgendaImportService.ImportResult runMeetingAgendaImport(
            int limit
    ) {
        var lastSync = syncStateService.getLastSuccessfulSync(
                SyncStateService.OPENPARLDATA_MEETINGS
        );

        var result =
                meetingAgendaImportService.importMeetingsWithAgendas(
                        0,
                        limit,
                        lastSync
                );

        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(
                    SyncStateService.OPENPARLDATA_MEETINGS,
                    result.maxUpdatedAt()
            );
        }

        return result;
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
            java.time.LocalDateTime maxUpdatedAt
    ) {
    }
}