CREATE TABLE import_job (
                            id UUID PRIMARY KEY,
                            job_type VARCHAR(100) NOT NULL,
                            status VARCHAR(50) NOT NULL,
                            started_at TIMESTAMP NOT NULL,
                            finished_at TIMESTAMP,
                            records_imported INTEGER DEFAULT 0,
                            records_updated INTEGER DEFAULT 0,
                            records_failed INTEGER DEFAULT 0,
                            error_message TEXT,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);