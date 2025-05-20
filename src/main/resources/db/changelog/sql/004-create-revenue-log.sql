CREATE TABLE revenue_log (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL UNIQUE REFERENCES parking_event(id),
    reference_date DATE NOT NULL,
    duration_minutes BIGINT NOT NULL,
    amount_charged DECIMAL(10, 2) NOT NULL,
    occupancy_rate DOUBLE PRECISION NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);
