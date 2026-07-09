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
                value(dto.title()),
                value(dto.title_long()),
                value(dto.type_name()),
                value(dto.type_harmonized()),
                value(dto.state_name()),
                parse(dto.begin_date()),
                parse(dto.end_date()),
                parse(dto.created_at()),
                parse(dto.updated_at()),
                dto.url_api(),
                value(dto.url_external())
        );
    }

    private String value(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        if (map.get("de") != null) return map.get("de");
        if (map.get("fr") != null) return map.get("fr");
        if (map.get("it") != null) return map.get("it");
        if (map.get("en") != null) return map.get("en");

        return map.values().stream().findFirst().orElse(null);
    }

    private LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}