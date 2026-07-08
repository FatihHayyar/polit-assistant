package ch.hslu.wipro.politassistant.adapter.in.rest;
import ch.hslu.wipro.politassistant.application.service.ImportOrchestratorService;
import ch.hslu.wipro.politassistant.application.service.AffairImportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dev/import")
class AffairImportDevController {

    private final AffairImportService importService;
    private final ImportOrchestratorService orchestratorService;

    AffairImportDevController(
            AffairImportService importService,
            ImportOrchestratorService orchestratorService
    ) {
        this.importService = importService;
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/affairs")
    ImportResult importAffairs(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        int imported = importService.importAffairs(offset, limit);
        return new ImportResult(imported);
    }

    record ImportResult(int imported) {}

    @PostMapping("/affairs/{id}/docs")
    ImportResult importDocs(@PathVariable Long id) {
        int imported = importService.importDocsForAffair(id);
        return new ImportResult(imported);
    }

    @PostMapping("/affairs/full")
    ImportOrchestratorService.FullImportResult importAffairsFull(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return orchestratorService.runFullImport(offset, limit);
    }
    @PostMapping("/affairs/incremental")
    ImportOrchestratorService.FullImportResult importIncremental(
            @RequestParam(defaultValue = "50") int limit
    ) {
        return orchestratorService.runIncrementalImport(limit);
    }
}