package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

public record OpenParlDataBulkAgendaDto(
        Long id,
        Long body_id,
        String body_key,
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
        String created_at
) {
}