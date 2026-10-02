package ch.hslu.wipro.politassistant.adapter.out.openparldata.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenParlDataBulkAffairDto(
        Long id,
        Long body_id,
        String body_key,
        String external_id,
        String number,

        String title_de,
        String title_fr,
        String title_it,
        String title_rm,

        String title_long_de,
        String title_long_fr,
        String title_long_it,
        String title_long_rm,

        String type_name_de,
        String type_name_fr,
        String type_name_it,
        String type_name_rm,

        String type_harmonized_de,
        String type_harmonized_fr,
        String type_harmonized_it,
        String type_harmonized_rm,
        String type_harmonized_en,

        String state_name_de,
        String state_name_fr,
        String state_name_it,
        String state_name_rm,

        String begin_date,
        String end_date,

        String url_external_de,
        String url_external_fr,
        String url_external_it,
        String url_external_rm,

        String created_at,
        String updated_at
) {
}