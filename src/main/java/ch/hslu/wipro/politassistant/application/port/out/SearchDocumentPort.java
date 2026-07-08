package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.classification.AffairSearchDocument;

public interface SearchDocumentPort {
    AffairSearchDocument load(Long affairId);
}