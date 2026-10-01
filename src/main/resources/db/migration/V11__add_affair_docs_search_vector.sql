ALTER TABLE affair_docs
    ADD COLUMN search_vector tsvector
        GENERATED ALWAYS AS (
            to_tsvector(
                    'german',
                    coalesce(name, '') || ' ' || coalesce(text_content, '')
            )
            ) STORED;

CREATE INDEX idx_affair_docs_search_vector
    ON affair_docs
    USING GIN (search_vector);