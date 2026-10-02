package ch.hslu.wipro.politassistant.adapter.out.openparldata.mapper;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataBulkAffairDto;
import ch.hslu.wipro.politassistant.domain.affair.Affair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OpenParlDataBulkAffairMapper {

    private static final String API_BASE_URL =
            "https://api.openparldata.ch/v1/affairs/";

    public Affair toDomain(OpenParlDataBulkAffairDto dto) {
        return new Affair(
                dto.id(),
                dto.body_key(),
                dto.body_id(),
                dto.external_id(),
                dto.number(),
                firstNonBlank(
                        dto.title_de(),
                        dto.title_fr(),
                        dto.title_it(),
                        dto.title_rm()
                ),
                firstNonBlank(
                        dto.title_long_de(),
                        dto.title_long_fr(),
                        dto.title_long_it(),
                        dto.title_long_rm()
                ),
                firstNonBlank(
                        dto.type_name_de(),
                        dto.type_name_fr(),
                        dto.type_name_it(),
                        dto.type_name_rm()
                ),
                firstNonBlank(
                        dto.type_harmonized_de(),
                        dto.type_harmonized_fr(),
                        dto.type_harmonized_it(),
                        dto.type_harmonized_en(),
                        dto.type_harmonized_rm()
                ),
                firstNonBlank(
                        dto.state_name_de(),
                        dto.state_name_fr(),
                        dto.state_name_it(),
                        dto.state_name_rm()
                ),
                parse(dto.begin_date()),
                parse(dto.end_date()),
                parse(dto.created_at()),
                parse(dto.updated_at()),
                API_BASE_URL + dto.id(),
                firstNonBlank(
                        dto.url_external_de(),
                        dto.url_external_fr(),
                        dto.url_external_it(),
                        dto.url_external_rm()
                )
        );
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    private LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value);
    }
}