package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.domain.classification.AffairSearchDocument;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcSearchDocumentRepository implements SearchDocumentPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSearchDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AffairSearchDocument load(Long affairId) {

        AffairData affair = jdbcTemplate.queryForObject(
                """
                SELECT
                    id,
                    title_de,
                    title_long_de
                FROM affairs
                WHERE id = ?
                """,
                (rs, rowNum) -> new AffairData(
                        rs.getLong("id"),
                        rs.getString("title_de"),
                        rs.getString("title_long_de")
                ),
                affairId
        );

        if (affair == null) {
            throw new IllegalArgumentException(
                    "Parlamentarisches Geschäft nicht gefunden: " + affairId
            );
        }

        List<String> documentContents = jdbcTemplate.query(
                """
                SELECT text_content
                FROM affair_docs
                WHERE affair_id = ?
                  AND text_content IS NOT NULL
                  AND text_content <> ''
                ORDER BY id
                """,
                (rs, rowNum) -> rs.getString("text_content"),
                affairId
        );

        return new AffairSearchDocument(
                affair.id(),
                affair.title(),
                affair.titleLong(),
                documentContents
        );
    }

    private record AffairData(
            Long id,
            String title,
            String titleLong
    ) {
    }
}