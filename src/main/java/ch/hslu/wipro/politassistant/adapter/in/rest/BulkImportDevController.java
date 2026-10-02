package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.BulkAffairImportService;
import ch.hslu.wipro.politassistant.application.service.BulkAgendaImportService;
import ch.hslu.wipro.politassistant.application.service.BulkDocImportOrchestratorService;
import ch.hslu.wipro.politassistant.application.service.BulkDocImportService;
import ch.hslu.wipro.politassistant.application.service.BulkMeetingImportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/dev/import/bulk")
public class BulkImportDevController {

    private static final Path DATA_DIRECTORY =
            Path.of("data", "openparldata");

    private static final Path AFFAIRS_FILE =
            DATA_DIRECTORY.resolve("affairs.ndjson.gz");

    private static final Path MEETINGS_FILE =
            DATA_DIRECTORY.resolve("meetings.ndjson.gz");

    private static final Path AGENDAS_FILE =
            DATA_DIRECTORY.resolve("agendas.ndjson.gz");

    private final BulkAffairImportService bulkAffairImportService;
    private final BulkDocImportService bulkDocImportService;
    private final BulkDocImportOrchestratorService bulkDocImportOrchestratorService;
    private final BulkMeetingImportService bulkMeetingImportService;
    private final BulkAgendaImportService bulkAgendaImportService;

    public BulkImportDevController(
            BulkAffairImportService bulkAffairImportService,
            BulkDocImportService bulkDocImportService,
            BulkDocImportOrchestratorService bulkDocImportOrchestratorService,
            BulkMeetingImportService bulkMeetingImportService,
            BulkAgendaImportService bulkAgendaImportService
    ) {
        this.bulkAffairImportService = bulkAffairImportService;
        this.bulkDocImportService = bulkDocImportService;
        this.bulkDocImportOrchestratorService =
                bulkDocImportOrchestratorService;
        this.bulkMeetingImportService = bulkMeetingImportService;
        this.bulkAgendaImportService = bulkAgendaImportService;
    }

    @PostMapping("/affairs")
    public BulkAffairImportService.BulkAffairImportResult importAffairs() {
        return bulkAffairImportService.importFile(AFFAIRS_FILE);
    }

    @PostMapping("/docs")
    public BulkDocImportService.BulkDocImportResult importSingleDocShard(
            @RequestParam String file
    ) {
        if (!file.matches("docs_[A-Za-z0-9]+\\.ndjson\\.gz")) {
            throw new IllegalArgumentException(
                    "Invalid bulk doc filename: " + file
            );
        }

        Path filePath = DATA_DIRECTORY.resolve(file);

        if (!filePath.normalize()
                .startsWith(DATA_DIRECTORY.normalize())) {
            throw new IllegalArgumentException(
                    "Invalid bulk doc path"
            );
        }

        return bulkDocImportService.importFile(filePath);
    }

    @PostMapping("/docs/all")
    public BulkDocImportOrchestratorService.BulkDocsImportResult importAllDocs() {
        return bulkDocImportOrchestratorService
                .importAllRequiredDocShards();
    }

    @PostMapping("/meetings")
    public BulkMeetingImportService.BulkMeetingImportResult importMeetings()
            throws Exception {
        return bulkMeetingImportService.importFile(MEETINGS_FILE);
    }

    @PostMapping("/agendas")
    public BulkAgendaImportService.BulkAgendaImportResult importAgendas()
            throws Exception {
        return bulkAgendaImportService.importFile(AGENDAS_FILE);
    }
}