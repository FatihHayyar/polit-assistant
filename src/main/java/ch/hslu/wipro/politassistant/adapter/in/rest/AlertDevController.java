package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import ch.hslu.wipro.politassistant.application.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "Alerts",
        description = "Alert generation and notification management"
)
@RestController
@RequestMapping("/api/v1/dev/alerts")
class AlertDevController {

    private final AlertJpaRepository repository;
    private final AlertService alertService;
    AlertDevController(AlertJpaRepository repository, AlertService alertService) {
        this.repository = repository;
        this.alertService = alertService;
    }
    @Operation(
            summary = "List pending alerts"
    )
    @GetMapping("/pending")
    List<?> pending() {
        return repository.findByStatus("PENDING");
    }
    @Operation(
            summary = "Send pending notifications",
            description = "Sends all pending or retryable notifications to the configured notification channel."
    )
    @PostMapping("/send-pending")
    SendResult sendPending() {
        int sent = alertService.sendPendingAlerts();
        return new SendResult(sent);
    }
    @GetMapping("/failed")
    List<?> failed() {
        return repository.findByStatus("FAILED");
    }
    @PostMapping("/retry")
    SendResult retryFailedAndPending() {
        int sent = alertService.sendPendingAlerts();
        return new SendResult(sent);
    }
    record SendResult(int sent) {}
}