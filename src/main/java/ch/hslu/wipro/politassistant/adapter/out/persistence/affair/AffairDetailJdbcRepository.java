package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairDetailResponse;
import ch.hslu.wipro.politassistant.domain.classification.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AffairDetailJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public AffairDetailJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AffairDetailResponse> findById(Long affairId) {

        List<AffairDetailResponse> affairs = jdbcTemplate.query(
                """
                SELECT
                    a.id,
                    a.number,
                    a.title_de,
                    a.title_long_de,
                    COALESCE(
                        a.type_name_de,
                        a.type_harmonized_de
                    ) AS affair_type,
                    a.state_name_de,
                    a.begin_date,
                    a.end_date,
                    a.url_external_de
                FROM affairs a
                WHERE a.id = ?
                """,
                (rs, rowNum) -> new AffairDetailResponse(
                        rs.getLong("id"),
                        rs.getString("number"),
                        rs.getString("title_de"),
                        rs.getString("title_long_de"),
                        rs.getString("affair_type"),
                        rs.getString("state_name_de"),

                        rs.getTimestamp("begin_date") == null
                                ? null
                                : rs.getTimestamp("begin_date").toLocalDateTime(),

                        rs.getTimestamp("end_date") == null
                                ? null
                                : rs.getTimestamp("end_date").toLocalDateTime(),

                        rs.getString("url_external_de"),

                        findTopics(affairId),
                        findDocuments(affairId)
                ),
                affairId
        );

        if (affairs.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(affairs.getFirst());
    }

    private List<AffairDetailResponse.TopicDetail> findTopics(Long affairId) {

        return jdbcTemplate.query(
                """
                SELECT
                    topic,
                    confidence,
                    classifier,
                    matched_keywords,
                    classified_at
                FROM affair_classifications
                WHERE affair_id = ?
                  AND topic <> 'SONSTIGES'
                ORDER BY confidence DESC, topic ASC
                """,
                (rs, rowNum) -> new AffairDetailResponse.TopicDetail(
                        toDisplayName(rs.getString("topic")),
                        rs.getBigDecimal("confidence"),
                        rs.getString("classifier"),
                        rs.getString("matched_keywords"),

                        rs.getTimestamp("classified_at") == null
                                ? null
                                : rs.getTimestamp("classified_at").toLocalDateTime()
                ),
                affairId
        );
    }

    private List<AffairDetailResponse.DocumentDetail> findDocuments(Long affairId) {

        return jdbcTemplate.query(
                """
                SELECT
                    id,
                    name,
                    doc_date,
                    format,
                    language,
                    text_content,
                    url
                FROM affair_docs
                WHERE affair_id = ?
                ORDER BY
                    doc_date DESC NULLS LAST,
                    id DESC
                """,
                (rs, rowNum) -> new AffairDetailResponse.DocumentDetail(
                        rs.getLong("id"),
                        rs.getString("name"),

                        rs.getTimestamp("doc_date") == null
                                ? null
                                : rs.getTimestamp("doc_date").toLocalDateTime(),

                        rs.getString("format"),
                        rs.getString("language"),
                        rs.getString("text_content"),
                        rs.getString("url")
                ),
                affairId
        );
    }

    private String toDisplayName(String databaseTopic) {

        try {
            return Topic
                    .valueOf(databaseTopic)
                    .getDisplayName();

        } catch (IllegalArgumentException e) {
            return databaseTopic;
        }
    }
}