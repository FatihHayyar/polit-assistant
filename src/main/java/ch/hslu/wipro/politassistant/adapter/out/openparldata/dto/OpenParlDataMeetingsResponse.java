package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import java.util.List;
import java.util.Map;

public record OpenParlDataMeetingsResponse(
        Meta meta,
        List<MeetingDto> data
) {

    public record Meta(
            int offset,
            int limit,
            int total_records,
            boolean has_more,
            String next_page
    ) {}

    public record MeetingDto(
            Long id,
            String url_api,
            String body_key,
            String external_id,
            String number,
            Map<String, String> name,
            Long body_id,
            String type,
            Long group_id,
            String begin_date,
            String end_date,
            String state,
            String location,
            String updated_at,
            String created_at,
            Map<String, String> url_external,
            Map<String, String> description,
            Map<String, String> links
    ) {}
}