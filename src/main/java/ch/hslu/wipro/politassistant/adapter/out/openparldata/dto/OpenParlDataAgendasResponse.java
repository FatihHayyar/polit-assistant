package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import java.util.List;
import java.util.Map;

public record OpenParlDataAgendasResponse(
        Meta meta,
        List<AgendaDto> data
) {

    public record Meta(
            int offset,
            int limit,
            int total_records,
            boolean has_more,
            String next_page
    ) {}

    public record AgendaDto(
            Long id,
            String url_api,
            String body_key,
            Long body_id,
            Long meeting_id,
            String item_date,
            String item_external_id,
            String item_title,
            String item_number_display,
            String item_number,
            String item_description,
            String item_status,
            String item_result,
            String item_category,
            String item_url,
            String item_affair_number,
            Long item_affair_id,
            String item_language,
            String created_at,
            Map<String, String> links
    ) {}
}