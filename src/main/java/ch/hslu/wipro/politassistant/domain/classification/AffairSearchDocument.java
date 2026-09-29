package ch.hslu.wipro.politassistant.domain.classification;

public record AffairSearchDocument(
        Long affairId,
        String title,
        String titleLong,
        String documentContent
) {}