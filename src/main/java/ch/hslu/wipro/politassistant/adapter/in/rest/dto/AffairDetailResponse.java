package ch.hslu.wipro.politassistant.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AffairDetailResponse(
        Long id,
        String number,
        String title,
        String titleLong,
        String type,
        String state,
        LocalDateTime beginDate,
        LocalDateTime endDate,
        String urlExternal,
        List<TopicDetail> topics,
        List<DocumentDetail> documents
) {

    public record TopicDetail(
            String name,
            BigDecimal confidence,
            String classifier,
            String matchedKeywords,
            LocalDateTime classifiedAt
    ) {
    }

    public record DocumentDetail(
            Long id,
            String name,
            LocalDateTime date,
            String format,
            String language,
            String text,
            String url
    ) {
    }
}