CREATE TABLE raw_affairs (
                             id BIGINT PRIMARY KEY,
                             source_json JSONB NOT NULL,
                             fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE affairs (
                         id BIGINT PRIMARY KEY,
                         body_key VARCHAR(50),
                         body_id BIGINT,
                         external_id TEXT,
                         number TEXT,
                         title_de TEXT,
                         title_long_de TEXT,
                         type_name_de TEXT,
                         type_harmonized_de TEXT,
                         state_name_de TEXT,
                         begin_date TIMESTAMP,
                         end_date TIMESTAMP,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP,
                         url_api TEXT,
                         url_external_de TEXT
);

CREATE INDEX idx_affairs_body_key ON affairs(body_key);
CREATE INDEX idx_affairs_begin_date ON affairs(begin_date);
CREATE INDEX idx_affairs_updated_at ON affairs(updated_at);
CREATE INDEX idx_affairs_type_name_de ON affairs(type_name_de);