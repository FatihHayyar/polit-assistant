package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.classification.AffairSearchDocument;

import java.util.List;
import java.util.Map;

public interface SearchDocumentPort {

    AffairSearchDocument load(Long affairId);

    Map<Long, AffairSearchDocument> loadBatch(List<Long> affairIds);
}