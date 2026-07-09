package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.application.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dev/dashboard")
@Tag(name = "Dashboard", description = "MVP dashboard summary")
class DashboardDevController {

    private final DashboardService dashboardService;

    DashboardDevController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    DashboardService.DashboardSummary summary() {
        return dashboardService.summary();
    }
}