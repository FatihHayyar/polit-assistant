package ch.hslu.wipro.politassistant.adapter.in.scheduler;

import ch.hslu.wipro.politassistant.application.service.AffairImportService;
import ch.hslu.wipro.politassistant.application.service.ImportJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenParlDataImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(OpenParlDataImportScheduler.class);
    private final ImportJobService importJobService;
    private final AffairImportService importService;

    public OpenParlDataImportScheduler(ImportJobService importJobService, AffairImportService importService) {
        this.importJobService = importJobService;
        this.importService = importService;
    }


    @Scheduled(cron = "0 30 6 * * *")
    public void runMorningImport() {
        var jobId = importJobService.start("OPENPARLDATA_AFFAIRS_FULL_IMPORT");

        try {
            var result = importService.importAffairsWithDocs(0, 50);

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