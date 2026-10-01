package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.RelevantAgendaItemResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.agenda.RelevantAgendaJdbcRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@Tag(
        name = "Traktanden",
        description = "Bevorstehende relevante Sessionstraktanden für WWF"
)
@RestController
@RequestMapping("/api/v1/agendas")
class AgendaController {

    private final RelevantAgendaJdbcRepository repository;

    AgendaController(
            RelevantAgendaJdbcRepository repository
    ) {
        this.repository = repository;
    }

    @Operation(
            summary = "Bevorstehende relevante Sessionstraktanden anzeigen",
            description = """
                    Zeigt relevante Traktanden innerhalb der kommenden
                    30 Tage.

                    Berücksichtigt werden nur parlamentarische Geschäfte,
                    die mindestens einem relevanten WWF-Thema zugeordnet sind.

                    Die zeitlich nächsten Traktanden werden zuerst angezeigt.
                    """
    )
    @GetMapping("/relevant")
    public List<RelevantAgendaItemResponse> findRelevantAgendaItems(

            @Parameter(
                    description = """
                            Optionales Startdatum im Format YYYY-MM-DD.
                            Ohne Angabe wird das heutige Datum verwendet.
                            """,
                    example = "2026-10-01"
            )
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate from,

            @Parameter(
                    description = "Maximale Anzahl Resultate (1–100)",
                    example = "5"
            )
            @RequestParam(defaultValue = "5")
            @Min(1)
            @Max(100)
            int limit,

            @Parameter(
                    description = "Anzahl zu überspringender Resultate",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            @Min(0)
            int offset
    ) {
        LocalDate effectiveFrom =
                from != null
                        ? from
                        : LocalDate.now();

        LocalDate until =
                effectiveFrom.plusDays(31);

        return repository.findRelevantAgendaItems(
                effectiveFrom.atStartOfDay(),
                until.atStartOfDay(),
                limit,
                offset
        );
    }
}