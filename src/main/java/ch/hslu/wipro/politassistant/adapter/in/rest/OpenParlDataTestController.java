package ch.hslu.wipro.politassistant.adapter.in.rest;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAffairsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OpenParlDataTestController {

    private final OpenParlDataClient client;

    OpenParlDataTestController(OpenParlDataClient client) {
        this.client = client;
    }

    @GetMapping("/api/v1/dev/openparldata/affairs")
    OpenParlDataAffairsResponse testAffairs() {
        return client.fetchAffairs(0, 5);
    }
}