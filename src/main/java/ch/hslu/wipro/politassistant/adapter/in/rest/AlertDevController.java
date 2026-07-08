package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.persistence.alert.AlertJpaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dev/alerts")
class AlertDevController {

    private final AlertJpaRepository repository;

    AlertDevController(AlertJpaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/pending")
    List<?> pending() {
        return repository.findByStatus("PENDING");
    }
}