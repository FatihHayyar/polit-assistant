package ch.hslu.wipro.politassistant.adapter.out.persistence.affair;

import ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AffairReadJpaRepository extends JpaRepository<AffairEntity, Long> {

    @Query("""
        SELECT new ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse(
            a.id,
            a.titleDe,
            a.typeNameDe,
            a.stateNameDe,
            c.topic,
            c.confidence,
            a.beginDate,
            a.urlExternalDe
        )
        FROM AffairEntity a
        LEFT JOIN ClassificationEntity c ON c.affairId = a.id
        WHERE (:topic IS NULL OR c.topic = :topic)
          AND (:q IS NULL OR LOWER(a.titleDe) LIKE LOWER(CONCAT('%', :q, '%')))
        ORDER BY a.beginDate DESC
        """)
    List<AffairSummaryResponse> search(
            @Param("topic") String topic,
            @Param("q") String q
    );
    @Query("""
    SELECT new ch.hslu.wipro.politassistant.adapter.in.rest.dto.AffairSummaryResponse(
        a.id,
        a.titleDe,
        a.typeNameDe,
        a.stateNameDe,
        c.topic,
        c.confidence,
        a.beginDate,
        a.urlExternalDe
    )
    FROM AffairEntity a
    LEFT JOIN ClassificationEntity c ON c.affairId = a.id
    WHERE a.id = :id
    """)
    List<AffairSummaryResponse> findSummaryById(@Param("id") Long id);
    @Query(value = """
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
WHERE
    (:topic IS NULL OR c.topic = :topic)
AND
(
    :q IS NULL
    OR
    a.search_vector @@ websearch_to_tsquery('german', :q)
)
ORDER BY
ts_rank(
    a.search_vector,
    websearch_to_tsquery('german', :q)
) DESC
""",
            nativeQuery = true)
    List<Object[]> searchFullText(
            @Param("topic") String topic,
            @Param("q") String q
    );
}