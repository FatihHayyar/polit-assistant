package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public record OpenParlDataBulkMeetingDto(
        Long id,
        Long body_id,
        String body_key,
        String external_id,
        String type,
        String parent_type,
        String parent_external_id,
        Long parent_oparl_id,
        String number,
        String abbreviation,
        String name_de,
        String name_fr,
        String name_it,
        String name_rm,
        String url_external_de,
        String url_external_fr,
        String url_external_it,
        String url_external_rm,
        Long group_id,
        String begin_date,
        String end_date,
        String state,
        String description_de,
        String description_fr,
        String description_it,
        String location,
        String type_external_de,
        String type_external_fr,
        String type_external_it,
        String updated_at,
        String updated_external_at,
        String created_at
) {
}