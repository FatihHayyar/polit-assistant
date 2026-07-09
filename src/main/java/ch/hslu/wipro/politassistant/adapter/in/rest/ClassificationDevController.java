package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.RuleBasedClassificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
@Tag(name = "Classification", description = "Topic classification endpoints")
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