package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import java.util.List;
import java.util.Map;

public record OpenParlDataAffairsResponse(
        Meta meta,
        List<AffairDto> data
) {
    public record Meta(
            int offset,
            int limit,
            int total_records,
            boolean has_more,
            String next_page
    ) {}

    public record AffairDto(
            Long id,
            String url_api,
            String body_key,
            String external_id,
            String number,
            Map<String, String> title,
            Long body_id,
            String begin_date,
            String end_date,
            String updated_at,
            String created_at,
            Map<String, String> title_long,
            Map<String, String> type_name,
            Map<String, String> type_harmonized,
            Map<String, String> state_name,
            Map<String, String> url_external,
            Map<String, String> links
    ) {}
}