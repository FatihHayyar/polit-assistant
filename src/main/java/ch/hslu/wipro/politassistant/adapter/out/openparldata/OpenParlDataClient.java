package ch.hslu.wipro.politassistant.adapter.out.openparldata;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAffairsResponse;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataDocsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAgendasResponse;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse;
@Component
public class OpenParlDataClient {

    private final RestClient restClient;
    private final String baseUrl;

    public OpenParlDataClient(
            RestClient restClient,
            @Value("${openparldata.base-url}") String baseUrl
    ) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    public OpenParlDataAffairsResponse fetchAffairs(int offset, int limit) {
        return fetchAffairs(offset, limit, "-begin_date");
    }

    public OpenParlDataAffairsResponse fetchLatestAffairs(int limit) {
        return fetchAffairs(0, limit, "-updated_at");
    }

    public OpenParlDataAffairsResponse fetchAffairs(
            int offset,
            int limit,
            String sortBy
    ) {
        return execute(
                "fetch affairs",
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/affairs/?offset={offset}&limit={limit}&sort_by={sortBy}",
                                offset,
                                limit,
                                sortBy
                        )
                        .retrieve()
                        .body(OpenParlDataAffairsResponse.class)
        );
    }

    public OpenParlDataDocsResponse fetchDocsForAffair(Long affairId) {
        return execute(
                "fetch docs for affair " + affairId,
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/affairs/{id}/docs",
                                affairId
                        )
                        .retrieve()
                        .body(OpenParlDataDocsResponse.class)
        );
    }
    public OpenParlDataMeetingsResponse fetchMeetings(int offset, int limit) {
        return execute(
                "fetch meetings",
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/meetings/?offset={offset}&limit={limit}",
                                offset,
                                limit
                        )
                        .retrieve()
                        .body(OpenParlDataMeetingsResponse.class)
        );
    }

    public OpenParlDataAgendasResponse fetchAgendasForMeeting(Long meetingId) {
        return execute(
                "fetch agendas for meeting " + meetingId,
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/meetings/{id}/agendas",
                                meetingId
                        )
                        .retrieve()
                        .body(OpenParlDataAgendasResponse.class)
        );
    }
    public OpenParlDataMeetingsResponse.MeetingDto fetchMeetingById(Long meetingId) {
        return execute(
                "fetch meeting " + meetingId,
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/meetings/{id}",
                                meetingId
                        )
                        .retrieve()
                        .body(OpenParlDataMeetingsResponse.MeetingDto.class)
        );
    }
    public OpenParlDataMeetingsResponse fetchMeetings(
            int offset,
            int limit,
            String sortBy
    ) {
        return execute(
                "fetch meetings sorted by " + sortBy,
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/meetings/?offset={offset}&limit={limit}&sort_by={sortBy}",
                                offset,
                                limit,
                                sortBy
                        )
                        .retrieve()
                        .body(OpenParlDataMeetingsResponse.class)
        );
    }
    private <T> T execute(
            String operation,
            java.util.function.Supplier<T> request
    ) {
        try {
            return request.get();
        } catch (Exception e) {
            throw new OpenParlDataClientException(
                    "OpenParlData request failed: " + operation,
                    e
            );
        }
    }
    public OpenParlDataAffairsResponse.AffairDto fetchAffairById(Long affairId) {
        return execute(
                "fetch affair " + affairId,
                () -> restClient.get()
                        .uri(
                                baseUrl + "/v1/affairs/{id}",
                                affairId
                        )
                        .retrieve()
                        .body(OpenParlDataAffairsResponse.AffairDto.class)
        );
    }
}