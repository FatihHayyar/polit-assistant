package ch.hslu.wipro.politassistant.application.service;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.OpenParlDataClient;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataMeetingsResponse;
import ch.hslu.wipro.politassistant.adapter.out.persistence.meeting.MeetingJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class MeetingImportService {

    private final OpenParlDataClient client;
    private final MeetingJdbcRepository repository;

    public MeetingImportService(
            OpenParlDataClient client,
            MeetingJdbcRepository repository
    ) {
        this.client = client;
        this.repository = repository;
    }

    public int importMeetings(int offset, int limit) {
        OpenParlDataMeetingsResponse response =
                client.fetchMeetings(offset, limit);

        if (response == null || response.data() == null) {
            return 0;
        }

        response.data().forEach(repository::upsert);

        return response.data().size();
    }
    public boolean importMeetingById(Long meetingId) {
        OpenParlDataMeetingsResponse.MeetingDto meeting =
                client.fetchMeetingById(meetingId);

        if (meeting == null) {
            return false;
        }

        repository.upsert(meeting);
        return true;
    }
}