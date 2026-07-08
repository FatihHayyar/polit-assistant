package ch.hslu.wipro.politassistant.adapter.in.scheduler;


import ch.hslu.wipro.politassistant.application.service.ImportJobService;
import ch.hslu.wipro.politassistant.application.service.ImportOrchestratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenParlDataImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(OpenParlDataImportScheduler.class);
    private final ImportJobService importJobService;
    private final ImportOrchestratorService orchestratorService;
    public OpenParlDataImportScheduler(
            ImportJobService importJobService,
            ImportOrchestratorService orchestratorService
    ) {
        this.importJobService = importJobService;
        this.orchestratorService = orchestratorService;
    }


    @Scheduled(cron = "0 30 6 * * *")
    public void runMorningImport() {
        var jobId = importJobService.start("OPENPARLDATA_AFFAIRS_FULL_IMPORT");

        try {
            var result = orchestratorService.runIncrementalImport(50);

            importJobService.success(
                    jobId,
                    result.affairsImported(),
                    0,
                    0
            );

        } catch (Exception e) {
            importJobService.failed(jobId, e.getMessage());
        }
    }
}