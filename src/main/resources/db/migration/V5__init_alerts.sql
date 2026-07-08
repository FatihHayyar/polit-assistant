CREATE TABLE alerts (
                        id UUID PRIMARY KEY,
                        affair_id BIGINT NOT NULL REFERENCES affairs(id),
                        topic VARCHAR(50) NOT NULL,
                        channel VARCHAR(30) NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        sent_at TIMESTAMP,
                        retry_count INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_alert_status ON alerts(status);
CREATE INDEX idx_alert_topic ON alerts(topic);
CREATE INDEX idx_alert_affair_id ON alerts(affair_id);