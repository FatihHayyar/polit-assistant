package ch.hslu.wipro.politassistant.adapter.out.persistence;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class RawAffairJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public RawAffairJdbcRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void upsert(Long id, Object source) {
        try {
            String json = objectMapper.writeValueAsString(source);

            jdbcTemplate.update("""
                INSERT INTO raw_affairs (id, source_json, fetched_at)
                VALUES (?, ?::jsonb, CURRENT_TIMESTAMP)
                ON CONFLICT (id)
                DO UPDATE SET
                    source_json = EXCLUDED.source_json,
                    fetched_at = CURRENT_TIMESTAMP
                """, id, json);

        } catch (Exception e) {
            throw new IllegalStateException("Could not persist raw affair " + id, e);
        }
    }
}