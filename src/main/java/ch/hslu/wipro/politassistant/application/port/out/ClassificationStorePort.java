package ch.hslu.wipro.politassistant.application.port.out;

import ch.hslu.wipro.politassistant.domain.classification.Topic;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ClassificationStorePort {

    void save(
            Long affairId,
            Topic topic,
            double confidence,
            String classifier,
            List<String> matchedKeywords
    );

    void deleteByAffairId(Long affairId);

    void replaceBatch(
            Map<Long, List<ClassificationToStore>> classificationsByAffair
    );

    Map<Long, Set<Topic>> findTopicsByAffairIds(List<Long> affairIds);

    record ClassificationToStore(
            Topic topic,
            double confidence,
            String classifier,
            List<String> matchedKeywords
    ) {
    }
}