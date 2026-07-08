package ch.hslu.wipro.politassistant.adapter.out.persistence.classification;

import java.io.Serializable;
import java.util.Objects;

public class ClassificationId implements Serializable {
    private Long affairId;
    private String topic;

    public ClassificationId() {}

    public ClassificationId(Long affairId, String topic) {
        this.affairId = affairId;
        this.topic = topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassificationId that)) return false;
        return Objects.equals(affairId, that.affairId)
                && Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(affairId, topic);
    }
}