package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairReadJpaRepository;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairSearchJdbcRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/affairs")
class AffairSearchController {

    private final AffairReadJpaRepository repository;
    private final AffairSearchJdbcRepository fullTextRepository;
    AffairSearchController(AffairReadJpaRepository repository, AffairSearchJdbcRepository fullTextRepository) {
        this.repository = repository;
        this.fullTextRepository = fullTextRepository;
    }

    @GetMapping
    public List<AffairSummaryResponse> search(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        if (q != null && !q.isBlank()) {
            return fullTextRepository.fullTextSearch(q, topic, limit, offset);
        }

        return fullTextRepository.filterByTopic(topic, limit, offset);

    }

}