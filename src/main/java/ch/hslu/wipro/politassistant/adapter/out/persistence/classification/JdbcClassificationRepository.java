package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import ch.hslu.wipro.politassistant.application.port.out.ClassificationStorePort;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class JdbcClassificationRepository
        implements ClassificationStorePort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassificationRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(
            Long affairId,
            Topic topic,
            double confidence,
            String classifier,
            List<String> matchedKeywords
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO affair_classifications (
                    affair_id,
                    topic,
                    confidence,
                    classifier,
                    matched_keywords,
                    classified_at
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

    @Override
    public void deleteByAffairId(Long affairId) {
        jdbcTemplate.update(
                """
                DELETE FROM affair_classifications
                WHERE affair_id = ?
                """,
                affairId
        );
    }

    @Override
    @Transactional
    public void replaceBatch(
            Map<Long, List<ClassificationToStore>> classificationsByAffair
    ) {
        if (classificationsByAffair == null
                || classificationsByAffair.isEmpty()) {
            return;
        }

        List<Long> affairIds =
                new ArrayList<>(classificationsByAffair.keySet());

        jdbcTemplate.batchUpdate(
                """
                DELETE FROM affair_classifications
                WHERE affair_id = ?
                """,
                affairIds,
                affairIds.size(),
                (statement, affairId) ->
                        statement.setLong(1, affairId)
        );

        List<ClassificationRow> rows = new ArrayList<>();

        classificationsByAffair.forEach(
                (affairId, classifications) -> {
                    for (ClassificationToStore classification : classifications) {
                        rows.add(
                                new ClassificationRow(
                                        affairId,
                                        classification
                                )
                        );
                    }
                }
        );

        if (rows.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO affair_classifications (
                    affair_id,
                    topic,
                    confidence,
                    classifier,
                    matched_keywords,
                    classified_at
                )
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (affair_id, topic)
                DO UPDATE SET
                    confidence = EXCLUDED.confidence,
                    classifier = EXCLUDED.classifier,
                    matched_keywords = EXCLUDED.matched_keywords,
                    classified_at = CURRENT_TIMESTAMP
                """,
                rows,
                rows.size(),
                (statement, row) -> {
                    statement.setLong(1, row.affairId());
                    statement.setString(
                            2,
                            row.classification().topic().name()
                    );
                    statement.setDouble(
                            3,
                            row.classification().confidence()
                    );
                    statement.setString(
                            4,
                            row.classification().classifier()
                    );
                    statement.setString(
                            5,
                            String.join(
                                    ",",
                                    row.classification().matchedKeywords()
                            )
                    );
                }
        );
    }

    @Override
    public Map<Long, Set<Topic>> findTopicsByAffairIds(
            List<Long> affairIds
    ) {
        Map<Long, Set<Topic>> result = new HashMap<>();

        if (affairIds == null || affairIds.isEmpty()) {
            return result;
        }

        Long[] ids = affairIds.toArray(Long[]::new);

        jdbcTemplate.query(
                """
                SELECT affair_id, topic
                FROM affair_classifications
                WHERE affair_id = ANY(?)
                """,
                ps -> ps.setArray(
                        1,
                        ps.getConnection().createArrayOf(
                                "bigint",
                                ids
                        )
                ),
                rs -> {
                    Long affairId = rs.getLong("affair_id");
                    String topicValue = rs.getString("topic");

                    try {
                        Topic topic = Topic.valueOf(topicValue);

                        result.computeIfAbsent(
                                affairId,
                                ignored -> new HashSet<>()
                        ).add(topic);
                    } catch (IllegalArgumentException ignored) {
                        // Ignore unknown historical topic values.
                    }
                }
        );

        return result;
    }

    private record ClassificationRow(
            Long affairId,
            ClassificationToStore classification
    ) {
    }
}