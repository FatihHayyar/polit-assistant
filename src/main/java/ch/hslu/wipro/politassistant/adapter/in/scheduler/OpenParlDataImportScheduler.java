package ch.hslu.wipro.politassistant.adapter.in.scheduler;

import ch.hslu.wipro.politassistant.application.service.AlertService;
import ch.hslu.wipro.politassistant.application.service.ImportJobService;
import ch.hslu.wipro.politassistant.application.service.ImportOrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenParlDataImportScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(OpenParlDataImportScheduler.class);

    private final ImportJobService importJobService;
    private final ImportOrchestratorService orchestratorService;
    private final AlertService alertService;

    public OpenParlDataImportScheduler(
            ImportJobService importJobService,
            ImportOrchestratorService orchestratorService,
            AlertService alertService
    ) {
        this.importJobService = importJobService;
        this.orchestratorService = orchestratorService;
        this.alertService = alertService;
    }

    @Scheduled(cron = "0 30 6 * * *")
    public void runMorningImport() {

        var jobId =
                importJobService.start(
                        "OPENPARLDATA_AFFAIRS_INCREMENTAL_IMPORT"
                );

        try {
            var result =
                    orchestratorService.runIncrementalImport(50);

            int sentNotifications =
                    alertService.sendPendingAlerts();

            importJobService.success(
                    jobId,
                    result.affairsImported(),
                    0,
                    sentNotifications
            );

            log.info(
                    "Affair import completed: imported={}, alertsCreated={}, notificationsSent={}",
                    result.affairsImported(),
                    result.alertsCreated(),
                    sentNotifications
            );

        } catch (Exception e) {
            importJobService.failed(jobId, e.getMessage());
            log.error("Affair import failed", e);
        }
    }

    @Scheduled(cron = "0 35 6 * * *")
    public void runMorningMeetingAgendaImport() {

        var jobId =
                importJobService.start(
                        "OPENPARLDATA_MEETINGS_INCREMENTAL_IMPORT"
                );

        try {
            var result =
                    orchestratorService.runMeetingAgendaImport(50);

            importJobService.success(
                    jobId,
                    result.importedMeetings(),
                    0,
                    0
            );

        } catch (Exception e) {
            importJobService.failed(jobId, e.getMessage());
            log.error("Meeting/Agenda import failed", e);
        }
    }
}