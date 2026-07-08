CREATE TABLE affair_classifications (
                                        affair_id BIGINT NOT NULL REFERENCES affairs(id),
                                        topic VARCHAR(50) NOT NULL,
                                        confidence NUMERIC(4,3) NOT NULL,
                                        classifier VARCHAR(50) NOT NULL,
                                        matched_keywords TEXT,
                                        classified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (affair_id, topic)
);

CREATE INDEX idx_affair_classifications_topic
    ON affair_classifications(topic);