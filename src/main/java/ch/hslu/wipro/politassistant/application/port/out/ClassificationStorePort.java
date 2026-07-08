package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.classification.Topic;

import java.util.List;

public interface ClassificationStorePort {
    void save(Long affairId, Topic topic, double confidence, String classifier, List<String> matchedKeywords);
    void deleteByAffairId(Long affairId);
}

