DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auction') THEN
        CREATE DATABASE auction;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS auction (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    init_value DOUBLE PRECISION NOT NULL,
    final_value DOUBLE PRECISION,
    end_date TIMESTAMP NOT NULL
);

