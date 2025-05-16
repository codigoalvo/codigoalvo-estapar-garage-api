CREATE TABLE spot (
    id UUID PRIMARY KEY,
    spot_number VARCHAR(20) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    is_occupied BOOLEAN NOT NULL DEFAULT false,
    sector_id UUID NOT NULL REFERENCES sector(id)
);
