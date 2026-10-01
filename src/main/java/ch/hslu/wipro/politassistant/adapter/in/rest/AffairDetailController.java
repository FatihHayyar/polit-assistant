package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairDetailResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.affair.AffairDetailJdbcRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Affairs",
        description = "Parlamentarische Geschäfte und Detailinformationen"
)
@RestController
@RequestMapping("/api/v1/affairs")
class AffairDetailController {

    private final AffairDetailJdbcRepository repository;

    AffairDetailController(
            AffairDetailJdbcRepository repository
    ) {
        this.repository = repository;
    }

    @Operation(
            summary = "Details eines parlamentarischen Geschäfts anzeigen",
            description = """
                    Liefert die im Polit-Assistant gespeicherten Informationen
                    zu einem parlamentarischen Geschäft.

                    Enthalten sind Stammdaten, WWF-Themenklassifikationen,
                    Klassifikationshinweise und bereits importierte Dokumente.
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<AffairDetailResponse> findById(
            @PathVariable Long id
    ) {
        return repository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(
                        () -> ResponseEntity
                                .notFound()
                                .build()
                );
    }
}