package ch.hslu.wipro.politassistant.adapter.in.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "System", description = "System health and utility endpoints")
@RestController
class PingController {

    @GetMapping("/api/v1/ping")
    String ping() {
        return "Polit Assistant is running";
    }
}