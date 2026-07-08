package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/affairs")
class AffairSearchController {

    private final AffairReadJpaRepository repository;

    AffairSearchController(AffairReadJpaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<AffairSummaryResponse> search(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String q
    ) {
        String cleanTopic = topic == null || topic.isBlank() ? null : topic;
        String cleanQ = q == null || q.isBlank() ? null : q;
        return repository.search(cleanTopic, cleanQ);
    }
}