DROP TABLE IF EXISTS announcement_reviews CASCADE;
DROP TABLE IF EXISTS announcement_likes CASCADE;
DROP TABLE IF EXISTS announcements CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255),
    cognome VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    ruolo VARCHAR(20),
    password VARCHAR(255)
);

CREATE TABLE properties (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(50),
    superficie_mq INTEGER,
    stanze INTEGER,
    bagni INTEGER,
    address VARCHAR(255),
    city VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TABLE announcements (
    id SERIAL PRIMARY KEY,
    titolo VARCHAR(255),
    descrizione TEXT,
    prezzo DOUBLE PRECISION,
    tipo VARCHAR(20),
    image_url TEXT,
    property_id INTEGER NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    vendor_id INTEGER REFERENCES users(id),
    data_pubblicazione DATE
);

CREATE TABLE announcement_likes (
    id SERIAL PRIMARY KEY,
    announcement_id INTEGER REFERENCES announcements(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_like UNIQUE (announcement_id, user_id)
);

CREATE TABLE announcement_reviews (
    id SERIAL PRIMARY KEY,
    announcement_id INTEGER REFERENCES announcements(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL,
    commento TEXT,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_review UNIQUE (announcement_id, user_id)
);
