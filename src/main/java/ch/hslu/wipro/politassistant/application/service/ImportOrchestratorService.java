package ch.hslu.wipro.politassistant.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ImportOrchestratorService {

    private final AffairImportService affairImportService;
    private final RuleBasedClassificationService classificationService;
    private final AlertService alertService;
    private final SyncStateService syncStateService;
    public ImportOrchestratorService(
            AffairImportService affairImportService,
            RuleBasedClassificationService classificationService,
            AlertService alertService, SyncStateService syncStateService
    ) {
        this.affairImportService = affairImportService;
        this.classificationService = classificationService;
        this.alertService = alertService;
        this.syncStateService = syncStateService;
    }

    @Transactional
    public FullImportResult runFullImport(int offset, int limit) {
        var result = affairImportService.importAffairsWithDocsOnly(offset, limit);

        int classified = classificationService.classifyAll(result.importedAffairIds());
        int alertsCreated = alertService.createAlertsForImportedAffairs(result.importedAffairIds());
        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(result.maxUpdatedAt());
        }
        return new FullImportResult(
                result.affairsImported(),
                result.docsImported(),
                classified,
                alertsCreated
        );
    }
    @Transactional
    public FullImportResult runIncrementalImport(int limit) {
        var lastSync = syncStateService.getLastSuccessfulSync();

        var result = affairImportService.importLatestAffairsOnly(limit, lastSync);

        int classified = classificationService.classifyAll(result.importedAffairIds());
        int alertsCreated = alertService.createAlertsForImportedAffairs(result.importedAffairIds());

        if (result.maxUpdatedAt() != null) {
            syncStateService.updateLastSuccessfulSync(result.maxUpdatedAt());
        }

        return new FullImportResult(
                result.affairsImported(),
                result.docsImported(),
                classified,
                alertsCreated
        );
    }
    public record FullImportResult(
            int affairsImported,
            int docsImported,
            int classified,
            int alertsCreated
    ) {}

    public record ImportResult(
            int affairsImported,
            int docsImported,
            ArrayList<Long> importedAffairIds,
            java.time.LocalDateTime maxUpdatedAt
    ) {}
}