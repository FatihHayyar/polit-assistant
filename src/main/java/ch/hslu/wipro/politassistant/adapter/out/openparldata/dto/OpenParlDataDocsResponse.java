package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import java.util.List;
import java.util.Map;

public record OpenParlDataDocsResponse(
        Meta meta,
        List<DocDto> data
) {
    public record Meta(
            int offset,
            int limit,
            int total_records,
            boolean has_more,
            String next_page
    ) {}

    public record DocDto(
            Long id,
            Long body_id,
            String body_key,
            String parent_type,
            String external_id,
            String external_alternative_id,
            String name,
            String url,
            String url_oparl,
            String date,
            Long size,
            String category_harmonized,
            String format,
            String language,
            String text,
            Long affair_id,
            Long meeting_id,
            Long agenda_id,
            Long news_id,
            Map<String, String> category
    ) {}
}