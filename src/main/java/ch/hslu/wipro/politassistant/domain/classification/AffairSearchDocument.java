package ch.hslu.wipro.politassistant.domain.classification;

import java.util.List;

public record AffairSearchDocument(
        Long affairId,
        String title,
        String titleLong,
        List<String> documentContents
) {
}