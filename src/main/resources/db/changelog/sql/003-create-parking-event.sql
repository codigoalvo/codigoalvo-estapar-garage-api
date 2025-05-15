CREATE TABLE parking_event (
    id UUID PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    event_type VARCHAR(10) NOT NULL CHECK (event_type IN ('ENTRY', 'PARKED', 'EXIT')),
    event_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    spot_id UUID REFERENCES spot(id),
    sector_id UUID REFERENCES sector(id)
);
