CREATE TABLE affair_docs (
                             id BIGINT PRIMARY KEY,
                             affair_id BIGINT NOT NULL REFERENCES affairs(id),
                             body_id BIGINT,
                             body_key VARCHAR(50),
                             name TEXT,
                             url TEXT,
                             url_oparl TEXT,
                             doc_date TIMESTAMP,
                             format TEXT,
                             language VARCHAR(20),
                             text_content TEXT,
                             raw_json JSONB NOT NULL,
                             fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_affair_docs_affair_id ON affair_docs(affair_id);
CREATE INDEX idx_affair_docs_body_key ON affair_docs(body_key);
CREATE INDEX idx_affair_docs_doc_date ON affair_docs(doc_date);