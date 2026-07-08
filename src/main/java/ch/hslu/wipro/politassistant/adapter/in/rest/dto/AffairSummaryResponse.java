package ch.hslu.wipro.politassistant.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AffairSummaryResponse(
        Long id,
        String title,
        String type,
        String state,
        String topic,
        BigDecimal confidence,
        LocalDateTime beginDate,
        String urlExternal
) {}