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
}