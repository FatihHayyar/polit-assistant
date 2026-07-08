package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class AffairSearchJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public AffairSearchJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AffairSummaryResponse> fullTextSearch(String query, String topic, int limit, int offset) {
        boolean hasTopic = topic != null && !topic.isBlank();

        StringBuilder sql = new StringBuilder("""
        SELECT
            a.id,
            a.title_de,
            a.type_name_de,
            a.state_name_de,
            c.topic,
            c.confidence,
            a.begin_date,
            a.url_external_de
        FROM affairs a
        LEFT JOIN affair_classifications c
            ON c.affair_id = a.id
        WHERE a.search_vector @@ websearch_to_tsquery('german', ?)
        """);

        var params = new java.util.ArrayList<Object>();
        params.add(query);

        if (hasTopic) {
            sql.append(" AND c.topic = ? ");
            params.add(topic);
        }

        sql.append("""
        ORDER BY ts_rank(
            a.search_vector,
            websearch_to_tsquery('german', ?)
        ) DESC
        LIMIT ?
        OFFSET ?
        """);

        params.add(query);
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new AffairSummaryResponse(
                        rs.getLong("id"),
                        rs.getString("title_de"),
                        rs.getString("type_name_de"),
                        rs.getString("state_name_de"),
                        rs.getString("topic"),
                        rs.getObject("confidence", java.math.BigDecimal.class),
                        rs.getTimestamp("begin_date") == null ? null : rs.getTimestamp("begin_date").toLocalDateTime(),
                        rs.getString("url_external_de")
                ),
                params.toArray()
        );
    }
    public List<AffairSummaryResponse> filterByTopic(String topic, int limit, int offset) {
        boolean hasTopic = topic != null && !topic.isBlank();

        StringBuilder sql = new StringBuilder("""
        SELECT
            a.id,
            a.title_de,
            a.type_name_de,
            a.state_name_de,
            c.topic,
            c.confidence,
            a.begin_date,
            a.url_external_de
        FROM affairs a
        LEFT JOIN affair_classifications c
            ON c.affair_id = a.id
        WHERE 1=1
        """);

        var params = new java.util.ArrayList<Object>();

        if (hasTopic) {
            sql.append(" AND c.topic = ? ");
            params.add(topic);
        }

        sql.append("""
        ORDER BY a.begin_date DESC NULLS LAST
        LIMIT ?
        OFFSET ?
        """);

        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> new AffairSummaryResponse(
                        rs.getLong("id"),
                        rs.getString("title_de"),
                        rs.getString("type_name_de"),
                        rs.getString("state_name_de"),
                        rs.getString("topic"),
                        rs.getObject("confidence", java.math.BigDecimal.class),
                        rs.getTimestamp("begin_date") == null ? null : rs.getTimestamp("begin_date").toLocalDateTime(),
                        rs.getString("url_external_de")
                ),
                params.toArray()
        );
    }

}