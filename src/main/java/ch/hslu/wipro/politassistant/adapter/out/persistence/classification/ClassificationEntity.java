package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "affair_classifications")
@IdClass(ClassificationId.class)
public class ClassificationEntity {

    @Id
    @Column(name = "affair_id")
    private Long affairId;

    @Id
    private String topic;

    private BigDecimal confidence;

    protected ClassificationEntity() {}

    public Long getAffairId() { return affairId; }
    public String getTopic() { return topic; }
    public BigDecimal getConfidence() { return confidence; }
}