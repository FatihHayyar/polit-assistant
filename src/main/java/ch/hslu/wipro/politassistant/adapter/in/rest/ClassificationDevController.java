package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.HistoricalClassificationService;
import ch.hslu.wipro.politassistant.application.service.RuleBasedClassificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Classification",
        description = "Topic classification endpoints"
)
@RestController
@RequestMapping("/api/v1/dev/classification")
class ClassificationDevController {

    private final RuleBasedClassificationService classificationService;
    private final HistoricalClassificationService historicalClassificationService;

    ClassificationDevController(
            RuleBasedClassificationService classificationService,
            HistoricalClassificationService historicalClassificationService
    ) {
        this.classificationService = classificationService;
        this.historicalClassificationService =
                historicalClassificationService;
    }

    @PostMapping("/affairs/{id}")
    RuleBasedClassificationService.ClassificationResult classify(
            @PathVariable Long id
    ) {
        return classificationService.classify(id);
    }

    @PostMapping("/historical")
    HistoricalClassificationService.HistoricalClassificationResult
    classifyHistorical() {

        return historicalClassificationService.classifyAll();
    }

    @GetMapping("/historical/status")
    HistoricalClassificationService.HistoricalClassificationStatus
    historicalStatus() {

        return historicalClassificationService.status();
    }

    @DeleteMapping("/historical/checkpoint")
    HistoricalClassificationService.HistoricalClassificationStatus
    resetHistoricalCheckpoint() {

        return historicalClassificationService.reset();
    }
}