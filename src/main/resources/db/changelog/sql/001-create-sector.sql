CREATE TABLE sector (
    id UUID PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    max_capacity INT NOT NULL,
    open_hour TIME NOT NULL,
    close_hour TIME NOT NULL,
    duration_limit_minutes INT NOT NULL
);
