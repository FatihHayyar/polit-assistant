package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(
        name = "Abonnements",
        description = "Sichere Verwaltung von WWF-Themenabonnements per E-Mail"
)
class SubscriptionController {

    private final UserPreferenceService service;

    SubscriptionController(
            UserPreferenceService service
    ) {
        this.service = service;
    }

    @Operation(
            summary = "Neues Abonnement anfordern",
            description = """
                    Fordert ein neues WWF-Themenabonnement an.
                    Die E-Mail-Adresse muss über einen per E-Mail
                    versendeten Link bestätigt werden, bevor das
                    Abonnement aktiviert wird.
                    """
    )
    @PostMapping
    UserPreferenceService.ActionResponse subscribe(
            @Valid @RequestBody SubscribeRequest request
    ) {
        return service.requestSubscription(
                request.email().trim(),
                normalizeDisplayName(request.displayName()),
                request.topics()
        );
    }

    @Operation(
            summary = "E-Mail-Adresse bestätigen",
            description = """
                    Bestätigt die E-Mail-Adresse über den zuvor
                    versendeten Bestätigungslink und aktiviert
                    das Abonnement.
                    """
    )
    @GetMapping("/verify")
    UserPreferenceService.SubscriptionResponse verify(
            @RequestParam
            @NotBlank(message = "Der Token darf nicht leer sein.")
            String token
    ) {
        return service.verifySubscription(token);
    }

    @Operation(
            summary = "Verwaltungslink anfordern",
            description = """
                    Sendet einen zeitlich begrenzten Verwaltungslink
                    an die angegebene E-Mail-Adresse, sofern ein
                    aktives Abonnement besteht.
                    """
    )
    @PostMapping("/manage")
    UserPreferenceService.ActionResponse requestManagementLink(
            @Valid @RequestBody ManagementRequest request
    ) {
        return service.requestManagementLink(
                request.email().trim()
        );
    }

    @Operation(
            summary = "Eigenes Abonnement anzeigen",
            description = """
                    Liefert das eigene aktive Abonnement.
                    Dafür ist ein gültiger Verwaltungs-Token erforderlich.
                    """
    )
    @GetMapping("/manage/{token}")
    UserPreferenceService.SubscriptionResponse getSubscription(
            @PathVariable
            @NotBlank(message = "Der Token darf nicht leer sein.")
            String token
    ) {
        return service.getSubscription(token);
    }

    @Operation(
            summary = "Eigenes Abonnement aktualisieren",
            description = """
                    Aktualisiert die abonnierten WWF-Themen.
                    Dafür ist ein gültiger Verwaltungs-Token erforderlich.
                    Nach erfolgreicher Änderung wird eine
                    Bestätigungs-E-Mail versendet.
                    """
    )
    @PutMapping("/manage/{token}")
    UserPreferenceService.SubscriptionResponse updateSubscription(
            @PathVariable
            @NotBlank(message = "Der Token darf nicht leer sein.")
            String token,

            @Valid @RequestBody UpdateSubscriptionRequest request
    ) {
        return service.updateSubscription(
                token,
                request.topics()
        );
    }

    @Operation(
            summary = "Abonnement löschen",
            description = """
                    Deaktiviert das eigene Abonnement.
                    Dafür ist ein gültiger Verwaltungs-Token erforderlich.
                    Nach erfolgreicher Deaktivierung wird eine
                    Bestätigungs-E-Mail versendet.
                    """
    )
    @DeleteMapping("/manage/{token}")
    UserPreferenceService.ActionResponse deleteSubscription(
            @PathVariable
            @NotBlank(message = "Der Token darf nicht leer sein.")
            String token
    ) {
        return service.deleteSubscription(token);
    }

    private String normalizeDisplayName(
            String displayName
    ) {
        if (displayName == null || displayName.isBlank()) {
            return null;
        }

        return displayName.trim();
    }

    record SubscribeRequest(

            @Schema(
                    description = "E-Mail-Adresse für Benachrichtigungen",
                    example = "max@beispiel.ch"
            )
            @NotBlank(
                    message = "Die E-Mail-Adresse darf nicht leer sein."
            )
            @Email(
                    message = "Die E-Mail-Adresse ist ungültig."
            )
            String email,

            @Schema(
                    description = "Optionaler Anzeigename",
                    example = "Max Muster"
            )
            String displayName,

            @ArraySchema(
                    schema = @Schema(
                            description = "WWF-Thema auf Deutsch",
                            example = "Wasser"
                    )
            )
            @NotEmpty(
                    message = "Mindestens ein Thema muss ausgewählt werden."
            )
            List<
                    @NotBlank(
                            message = "Ein Thema darf nicht leer sein."
                    )
                            String
                    > topics
    ) {
    }

    record ManagementRequest(

            @Schema(
                    description = "E-Mail-Adresse des Abonnements",
                    example = "max@beispiel.ch"
            )
            @NotBlank(
                    message = "Die E-Mail-Adresse darf nicht leer sein."
            )
            @Email(
                    message = "Die E-Mail-Adresse ist ungültig."
            )
            String email
    ) {
    }

    record UpdateSubscriptionRequest(

            @ArraySchema(
                    schema = @Schema(
                            description = "Neue Auswahl der WWF-Themen",
                            example = "Klima"
                    )
            )
            @NotEmpty(
                    message = "Mindestens ein Thema muss ausgewählt werden."
            )
            List<
                    @NotBlank(
                            message = "Ein Thema darf nicht leer sein."
                    )
                            String
                    > topics
    ) {
    }
}