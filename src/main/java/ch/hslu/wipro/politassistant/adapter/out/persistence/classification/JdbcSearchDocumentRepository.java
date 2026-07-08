package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import ch.hslu.wipro.politassistant.application.port.out.SearchDocumentPort;
import ch.hslu.wipro.politassistant.domain.classification.AffairSearchDocument;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcSearchDocumentRepository implements SearchDocumentPort {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSearchDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AffairSearchDocument load(Long affairId) {
        return jdbcTemplate.queryForObject("""
            SELECT
                a.id,
                a.title_de,
                CONCAT_WS(' ', a.title_de, a.title_long_de, STRING_AGG(d.text_content, ' '))
            FROM affairs a
            LEFT JOIN affair_docs d ON d.affair_id = a.id
            WHERE a.id = ?
            GROUP BY a.id, a.title_de, a.title_long_de
            """,
                (rs, rowNum) -> new AffairSearchDocument(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3)
                ),
                affairId
        );
    }
}