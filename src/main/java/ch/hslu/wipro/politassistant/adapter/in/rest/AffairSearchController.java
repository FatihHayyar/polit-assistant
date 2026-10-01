package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairSearchJdbcRepository;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Tag(
        name = "Affairs",
        description = "Parlamentarische Geschäfte suchen und nach WWF-Themen filtern"
)
@RestController
@RequestMapping("/api/v1/affairs")
class AffairSearchController {

    private final AffairSearchJdbcRepository searchRepository;

    AffairSearchController(
            AffairSearchJdbcRepository searchRepository
    ) {
        this.searchRepository = searchRepository;
    }

    @Operation(
            summary = "Parlamentarische Geschäfte suchen und filtern",
            description = """
                    Durchsucht parlamentarische Geschäfte und ermöglicht
                    die Filterung nach WWF-Themen.

                    'q' führt eine freie Volltextsuche durch.
                    'topic' filtert nach einem klassifizierten WWF-Thema.

                    Beide Parameter können kombiniert werden.
                    """
    )
    @GetMapping
    public List<AffairSummaryResponse> search(

            @Parameter(
                    description = """
                            WWF-Thema auf Deutsch.
                            Verfügbare Themen: Energie, Biodiversität, Wasser,
                            Landwirtschaft, Raumplanung, Klima, Mobilität und Abfall.
                            """,
                    example = "Wasser"
            )
            @RequestParam(required = false)
            String topic,

            @Parameter(
                    description = "Freier Suchbegriff für die Volltextsuche",
                    example = "Pestizide"
            )
            @RequestParam(required = false)
            String q,

            @Parameter(
                    description = "Maximale Anzahl Resultate (1–100)",
                    example = "20"
            )
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int limit,

            @Parameter(
                    description = "Anzahl zu überspringender Resultate für die Seitennavigation",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            @Min(0)
            int offset
    ) {
        Topic parsedTopic = Topic.fromDisplayName(topic);

        String databaseTopic =
                parsedTopic == null
                        ? null
                        : parsedTopic.name();

        if (q != null && !q.isBlank()) {
            return searchRepository.fullTextSearch(
                    q.trim(),
                    databaseTopic,
                    limit,
                    offset
            );
        }

        return searchRepository.filterByTopic(
                databaseTopic,
                limit,
                offset
        );
    }
}