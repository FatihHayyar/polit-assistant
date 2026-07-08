package ch.hslu.wipro.politassistant.adapter.in.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PingController {

    @GetMapping("/api/v1/ping")
    String ping() {
        return "Polit Assistant is running";
    }
}