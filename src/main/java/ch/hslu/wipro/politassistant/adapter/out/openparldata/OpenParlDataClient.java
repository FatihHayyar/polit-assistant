package ch.hslu.wipro.politassistant.adapter.out.openparldata;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAffairsResponse;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataDocsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenParlDataClient {

    private final RestClient restClient;
    private final String baseUrl;

    public OpenParlDataClient(RestClient restClient,
                              @Value("${openparldata.base-url}") String baseUrl) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    public OpenParlDataAffairsResponse fetchAffairs(int offset, int limit) {
        return restClient.get()
                .uri(baseUrl + "/v1/affairs/?offset={offset}&limit={limit}", offset, limit)
                .retrieve()
                .body(OpenParlDataAffairsResponse.class);
    }
    public OpenParlDataDocsResponse fetchDocsForAffair(Long affairId) {
        return restClient.get()
                .uri(baseUrl + "/v1/affairs/{id}/docs", affairId)
                .retrieve()
                .body(OpenParlDataDocsResponse.class);
    }
}