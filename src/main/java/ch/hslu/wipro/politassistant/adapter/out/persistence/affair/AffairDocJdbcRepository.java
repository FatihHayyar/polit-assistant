package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataDocsResponse.DocDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Repository
public class AffairDocJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AffairDocJdbcRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void upsert(DocDto doc) {
        try {
            String json = objectMapper.writeValueAsString(doc);

            jdbcTemplate.update("""
                INSERT INTO affair_docs (
                    id, affair_id, body_id, body_key, name,
                    url, url_oparl, doc_date, format, language,
                    text_content, raw_json, fetched_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, CURRENT_TIMESTAMP)
                ON CONFLICT (id)
                DO UPDATE SET
                    affair_id = EXCLUDED.affair_id,
                    body_id = EXCLUDED.body_id,
                    body_key = EXCLUDED.body_key,
                    name = EXCLUDED.name,
                    url = EXCLUDED.url,
                    url_oparl = EXCLUDED.url_oparl,
                    doc_date = EXCLUDED.doc_date,
                    format = EXCLUDED.format,
                    language = EXCLUDED.language,
                    text_content = EXCLUDED.text_content,
                    raw_json = EXCLUDED.raw_json,
                    fetched_at = CURRENT_TIMESTAMP
                """,
                    doc.id(),
                    doc.affair_id(),
                    doc.body_id(),
                    doc.body_key(),
                    doc.name(),
                    doc.url(),
                    doc.url_oparl(),
                    parse(doc.date()),
                    doc.format(),
                    doc.language(),
                    doc.text(),
                    json
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not persist doc " + doc.id(), e);
        }
    }

    private LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}