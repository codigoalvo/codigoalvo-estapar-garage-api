CREATE TABLE revenue_log (
    id UUID PRIMARY KEY,
    sector_id UUID NOT NULL REFERENCES sector(id),
    reference_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    UNIQUE (sector_id, reference_date)
);
