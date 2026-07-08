ALTER TABLE affairs
    ADD COLUMN search_vector tsvector;

UPDATE affairs
SET search_vector =
        to_tsvector(
                'german',
                coalesce(title_de,'') || ' ' ||
                coalesce(title_long_de,'')
        );

CREATE INDEX idx_affairs_search
    ON affairs
    USING GIN(search_vector);
CREATE FUNCTION affairs_search_vector_update()
    RETURNS trigger AS
    $$
BEGIN
    NEW.search_vector :=
        to_tsvector(
            'german',
            coalesce(NEW.title_de,'') || ' ' ||
            coalesce(NEW.title_long_de,'')
        );

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_affairs_search_vector
    BEFORE INSERT OR UPDATE
                         ON affairs
                         FOR EACH ROW
                         EXECUTE FUNCTION affairs_search_vector_update();