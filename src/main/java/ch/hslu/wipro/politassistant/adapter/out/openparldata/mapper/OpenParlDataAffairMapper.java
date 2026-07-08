package ch.hslu.wipro.politassistant.adapter.out.openparldata.mapper;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataAffairsResponse.AffairDto;
import ch.hslu.wipro.politassistant.domain.affair.Affair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class OpenParlDataAffairMapper {

    public Affair toDomain(AffairDto dto) {
        return new Affair(
                dto.id(),
                dto.body_key(),
                dto.body_id(),
                dto.external_id(),
                dto.number(),
                value(dto.title(), "de"),
                value(dto.title_long(), "de"),
                value(dto.type_name(), "de"),
                value(dto.type_harmonized(), "de"),
                value(dto.state_name(), "de"),
                parse(dto.begin_date()),
                parse(dto.end_date()),
                parse(dto.created_at()),
                parse(dto.updated_at()),
                dto.url_api(),
                value(dto.url_external(), "de")
        );
    }

    private String value(Map<String, String> map, String key) {
        return map == null ? null : map.get(key);
    }

    private LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}