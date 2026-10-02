package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataBulkDocDto;
import ch.hslu.wipro.politassistant.adapter.out.openparldata.dto.OpenParlDataDocsResponse.DocDto;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class AffairDocJdbcRepository {

    private static final String UPSERT_SQL = """
            INSERT INTO affair_docs (
                id,
                affair_id,
                body_id,
                body_key,
                name,
                url,
                url_oparl,
                doc_date,
                format,
                language,
                text_content,
                raw_json,
                fetched_at
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
            """;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AffairDocJdbcRepository(
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void upsert(DocDto doc) {
        try {
            PersistableDoc persistableDoc =
                    new PersistableDoc(
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
                            objectMapper.writeValueAsString(doc)
                    );

            persist(persistableDoc);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not persist doc " + doc.id(),
                    e
            );
        }
    }

    public void upsert(OpenParlDataBulkDocDto doc) {
        try {
            PersistableDoc persistableDoc =
                    toPersistableDoc(doc);

            persist(persistableDoc);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not persist bulk doc " + doc.id(),
                    e
            );
        }
    }

    public void upsertBatch(
            List<OpenParlDataBulkDocDto> docs
    ) {
        if (docs == null || docs.isEmpty()) {
            return;
        }

        List<PersistableDoc> persistableDocs =
                new ArrayList<>(docs.size());

        try {
            for (OpenParlDataBulkDocDto doc : docs) {
                persistableDocs.add(
                        toPersistableDoc(doc)
                );
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not prepare bulk doc batch",
                    e
            );
        }

        jdbcTemplate.batchUpdate(
                UPSERT_SQL,
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(
                            PreparedStatement ps,
                            int i
                    ) throws SQLException {
                        bind(
                                ps,
                                persistableDocs.get(i)
                        );
                    }

                    @Override
                    public int getBatchSize() {
                        return persistableDocs.size();
                    }
                }
        );
    }

    public Set<Long> findExistingIds(
            List<Long> documentIds
    ) {
        Set<Long> result = new HashSet<>();

        if (documentIds == null || documentIds.isEmpty()) {
            return result;
        }

        String placeholders =
                String.join(
                        ",",
                        java.util.Collections.nCopies(
                                documentIds.size(),
                                "?"
                        )
                );

        String sql =
                "SELECT id FROM affair_docs WHERE id IN (" +
                        placeholders +
                        ")";

        jdbcTemplate.query(
                sql,
                rs -> {
                    while (rs.next()) {
                        result.add(
                                rs.getLong("id")
                        );
                    }
                },
                documentIds.toArray()
        );

        return result;
    }

    private PersistableDoc toPersistableDoc(
            OpenParlDataBulkDocDto doc
    ) throws Exception {
        return new PersistableDoc(
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
                objectMapper.writeValueAsString(doc)
        );
    }

    private void persist(
            PersistableDoc doc
    ) {
        jdbcTemplate.update(
                UPSERT_SQL,
                doc.id(),
                doc.affairId(),
                doc.bodyId(),
                doc.bodyKey(),
                doc.name(),
                doc.url(),
                doc.urlOparl(),
                doc.date(),
                doc.format(),
                doc.language(),
                doc.text(),
                doc.rawJson()
        );
    }

    private void bind(
            PreparedStatement ps,
            PersistableDoc doc
    ) throws SQLException {
        ps.setObject(1, doc.id());
        ps.setObject(2, doc.affairId());
        ps.setObject(3, doc.bodyId());
        ps.setString(4, doc.bodyKey());
        ps.setString(5, doc.name());
        ps.setString(6, doc.url());
        ps.setString(7, doc.urlOparl());

        if (doc.date() == null) {
            ps.setTimestamp(8, null);
        } else {
            ps.setTimestamp(
                    8,
                    Timestamp.valueOf(doc.date())
            );
        }

        ps.setString(9, doc.format());
        ps.setString(10, doc.language());
        ps.setString(11, doc.text());
        ps.setString(12, doc.rawJson());
    }

    private LocalDateTime parse(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(value);
    }

    private record PersistableDoc(
            Long id,
            Long affairId,
            Long bodyId,
            String bodyKey,
            String name,
            String url,
            String urlOparl,
            LocalDateTime date,
            String format,
            String language,
            String text,
            String rawJson
    ) {
    }
}