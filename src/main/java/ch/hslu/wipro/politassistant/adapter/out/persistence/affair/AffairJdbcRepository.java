package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.application.port.out.AffairStorePort;
import ch.hslu.wipro.politassistant.domain.affair.Affair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AffairJdbcRepository implements AffairStorePort {

    private final JdbcTemplate jdbcTemplate;

    public AffairJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void upsert(Affair affair) {
        jdbcTemplate.update("""
            INSERT INTO affairs (
                id, body_key, body_id, external_id, number,
                title_de, title_long_de, type_name_de, type_harmonized_de, state_name_de,
                begin_date, end_date, created_at, updated_at,
                url_api, url_external_de
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id)
            DO UPDATE SET
                body_key = EXCLUDED.body_key,
                body_id = EXCLUDED.body_id,
                external_id = EXCLUDED.external_id,
                number = EXCLUDED.number,
                title_de = EXCLUDED.title_de,
                title_long_de = EXCLUDED.title_long_de,
                type_name_de = EXCLUDED.type_name_de,
                type_harmonized_de = EXCLUDED.type_harmonized_de,
                state_name_de = EXCLUDED.state_name_de,
                begin_date = EXCLUDED.begin_date,
                end_date = EXCLUDED.end_date,
                created_at = EXCLUDED.created_at,
                updated_at = EXCLUDED.updated_at,
                url_api = EXCLUDED.url_api,
                url_external_de = EXCLUDED.url_external_de
            """,
                affair.id(),
                affair.bodyKey(),
                affair.bodyId(),
                affair.externalId(),
                affair.number(),
                affair.titleDe(),
                affair.titleLongDe(),
                affair.typeNameDe(),
                affair.typeHarmonizedDe(),
                affair.stateNameDe(),
                affair.beginDate(),
                affair.endDate(),
                affair.createdAt(),
                affair.updatedAt(),
                affair.urlApi(),
                affair.urlExternalDe()
        );
    }
}