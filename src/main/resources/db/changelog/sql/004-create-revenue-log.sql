CREATE TABLE revenue_log (
    id UUID PRIMARY KEY,
    entry_event_id UUID NOT NULL UNIQUE REFERENCES parking_event(id),
    parked_event_id UUID NOT NULL UNIQUE REFERENCES parking_event(id),
    exit_event_id UUID NOT NULL UNIQUE REFERENCES parking_event(id),
    reference_date DATE NOT NULL,
    duration_minutes BIGINT NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    occupancy_rate DOUBLE PRECISION NOT NULL,
    occupancy_multiplier NUMERIC(5, 2) NOT NULL,
    period_multiplier NUMERIC(5, 2) NOT NULL,
    amount_charged DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

