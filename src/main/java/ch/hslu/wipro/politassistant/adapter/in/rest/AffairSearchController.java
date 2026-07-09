package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairSearchJdbcRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Affairs", description = "Search and filter parliamentary affairs")
@RestController
@RequestMapping("/api/v1/affairs")
class AffairSearchController {

    private final AffairSearchJdbcRepository searchRepository;

    AffairSearchController(AffairSearchJdbcRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Operation(summary = "Search parliamentary affairs")
    @GetMapping
    public List<AffairSummaryResponse> search(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        if (q != null && !q.isBlank()) {
            return searchRepository.fullTextSearch(q, topic, limit, offset);
        }

        return searchRepository.filterByTopic(topic, limit, offset);
    }
}