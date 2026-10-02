package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenParlDataBulkDocDto(
        Long id,
        String url,
        String date,
        String hash,
        String name,
        Long size,
        String text,
        String format,
        Long body_id,
        Long news_id,
        String body_key,
        String language,
        Long affair_id,
        Long agenda_id,
        String url_oparl,
        Long meeting_id,
        String updated_at,
        String external_id,
        String parent_type,
        String category_harmonized
) {
}