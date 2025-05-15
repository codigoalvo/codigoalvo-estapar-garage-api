CREATE TABLE revenue_log (
    id UUID PRIMARY KEY,
    sector_id UUID NOT NULL REFERENCES sector(id),
    reference_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'BRL',
    timestamp TIMESTAMP DEFAULT now()
);
