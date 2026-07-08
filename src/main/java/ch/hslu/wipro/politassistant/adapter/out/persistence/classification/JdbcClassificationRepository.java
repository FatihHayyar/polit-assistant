package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcClassificationRepository implements ClassificationStorePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Long affairId, Topic topic, double confidence, String classifier, List<String> matchedKeywords) {
        jdbcTemplate.update("""
            INSERT INTO affair_classifications (
                affair_id, topic, confidence, classifier, matched_keywords, classified_at
            )
            VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (affair_id, topic)
            DO UPDATE SET
                confidence = EXCLUDED.confidence,
                classifier = EXCLUDED.classifier,
                matched_keywords = EXCLUDED.matched_keywords,
                classified_at = CURRENT_TIMESTAMP
            """,
                affairId,
                topic.name(),
                confidence,
                classifier,
                String.join(",", matchedKeywords)
        );
    }
    public void deleteByAffairId(Long affairId) {
        jdbcTemplate.update(
                "DELETE FROM affair_classifications WHERE affair_id = ?",
                affairId
        );
    }
}