package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.RuleBasedClassificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dev/classification")
class ClassificationDevController {

    private final RuleBasedClassificationService service;

    ClassificationDevController(RuleBasedClassificationService service) {
        this.service = service;
    }

    @PostMapping("/affairs/{id}")
    RuleBasedClassificationService.ClassificationResult classify(@PathVariable Long id) {
        return service.classify(id);
    }
}