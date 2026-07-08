package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import ch.hslu.wipro.politassistant.application.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dev/alerts")
class AlertDevController {

    private final AlertJpaRepository repository;
    private final AlertService alertService;
    AlertDevController(AlertJpaRepository repository, AlertService alertService) {
        this.repository = repository;
        this.alertService = alertService;
    }

    @GetMapping("/pending")
    List<?> pending() {
        return repository.findByStatus("PENDING");
    }
    @PostMapping("/send-pending")
    SendResult sendPending() {
        int sent = alertService.sendPendingAlerts();
        return new SendResult(sent);
    }

    record SendResult(int sent) {}
}