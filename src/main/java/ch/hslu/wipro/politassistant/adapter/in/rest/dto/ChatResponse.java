package ch.hslu.wipro.politassistant.adapter.in.rest.dto;

import java.util.List;

public record ChatResponse(
        String answer,
        String detectedTopic,
        List<AffairSummaryResponse> results
) {
}