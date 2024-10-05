DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auction') THEN
        CREATE DATABASE auction;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS auctions (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    init_value DOUBLE PRECISION NOT NULL,
    final_value DOUBLE PRECISION,
    end_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birthdate TIMESTAMP NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS bids (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    auction_id INTEGER NOT NULL,
    date TIMESTAMP NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (auction_id) REFERENCES auctions(id)
);
