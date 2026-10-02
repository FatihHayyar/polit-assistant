package ch.hslu.wipro.politassistant.adapter.in.scheduler;

import ch.hslu.wipro.politassistant.application.service.ImportJobService;
import ch.hslu.wipro.politassistant.application.service.UpdateWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenParlDataImportScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    OpenParlDataImportScheduler.class
            );

    private final ImportJobService importJobService;
    private final UpdateWorkflowService updateWorkflowService;

    public OpenParlDataImportScheduler(
            ImportJobService importJobService,
            UpdateWorkflowService updateWorkflowService
    ) {
        this.importJobService =
                importJobService;

        this.updateWorkflowService =
                updateWorkflowService;
    }

    @Scheduled(cron = "0 30 6 * * *")
    public void runMorningUpdate() {

        var jobId =
                importJobService.start(
                        "OPENPARLDATA_INCREMENTAL_UPDATE"
                );

        try {
            var result =
                    updateWorkflowService.runUpdate(
                            50,
                            50
                    );

            importJobService.success(
                    jobId,
                    result.affairsUpdated()
                            + result.meetingsUpdated(),
                    0,
                    result.notificationsSent()
            );

            log.info(
                    "Scheduled OpenParlData update completed: status={}, affairs={}, documents={}, meetings={}, agendas={}, alertsCreated={}, notificationsSent={}",
                    result.status(),
                    result.affairsUpdated(),
                    result.documentsImported(),
                    result.meetingsUpdated(),
                    result.agendasImported(),
                    result.alertsCreated(),
                    result.notificationsSent()
            );

        } catch (Exception exception) {

            importJobService.failed(
                    jobId,
                    exception.getMessage()
            );

            log.error(
                    "Scheduled OpenParlData update failed",
                    exception
            );
        }
    }
}