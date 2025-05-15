CREATE TABLE sector (
    id UUID PRIMARY KEY,
    name VARCHAR(10) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    max_capacity INTEGER NOT NULL,
    open_hour TIME NOT NULL,
    close_hour TIME NOT NULL,
    duration_limit_minutes INTEGER NOT NULL
);
