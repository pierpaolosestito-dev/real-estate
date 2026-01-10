-- =========================================================
-- CLEAN (ordine corretto per i vincoli FK)
-- =========================================================
DELETE FROM announcement_reviews;
DELETE FROM announcement_likes;
DELETE FROM announcements;
DELETE FROM properties;
DELETE FROM users;

-- =========================================================
-- USERS (with clear password)
-- =========================================================
INSERT INTO users (nome, cognome, email, ruolo, password) VALUES
('Admin', 'System', 'admin@realestate.it', 'ADMIN', 'admin123'),
('Mario', 'Rossi', 'mario@realestate.it', 'VENDITORE', 'mario123'),
('Anna', 'Bianchi', 'anna@realestate.it', 'ACQUIRENTE', 'anna123');

-- =========================================================
-- PROPERTIES
-- =========================================================
INSERT INTO properties (
    tipo,
    superficie_mq,
    stanze,
    bagni,
    address,
    city,
    latitude,
    longitude
) VALUES
(
    'APPARTAMENTO',
    90,
    4,
    1,
    'Via Roma 10',
    'Roma',
    41.9028,
    12.4964
),
(
    'MONOLOCALE',
    40,
    1,
    1,
    'Via Milano 5',
    'Milano',
    45.4642,
    9.1900
);

-- =========================================================
-- ANNOUNCEMENTS
-- =========================================================
INSERT INTO announcements (
    titolo,
    descrizione,
    prezzo,
    tipo,
    image_url,
    property_id,
    vendor_id,
    data_pubblicazione
)
SELECT
    'Appartamento centrale a Roma',
    'Appartamento luminoso in zona centrale, vicino ai servizi.',
    250000,
    'VENDITA',
    'https://images.unsplash.com/photo-1568605114967-8130f3a36994',
    p.id,
    u.id,
    CURRENT_DATE
FROM properties p, users u
WHERE p.address = 'Via Roma 10'
  AND u.email = 'mario@realestate.it';

INSERT INTO announcements (
    titolo,
    descrizione,
    prezzo,
    tipo,
    image_url,
    property_id,
    vendor_id,
    data_pubblicazione
)
SELECT
    'Monolocale in affitto a Milano',
    'Ideale per studenti o giovani lavoratori.',
    700,
    'AFFITTO',
    'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267',
    p.id,
    u.id,
    CURRENT_DATE
FROM properties p, users u
WHERE p.address = 'Via Milano 5'
  AND u.email = 'mario@realestate.it';

-- =========================================================
-- ANNOUNCEMENT LIKES
-- (created_at valorizzato esplicitamente)
-- =========================================================
INSERT INTO announcement_likes (
    announcement_id,
    user_id,
    created_at
)
SELECT
    a.id,
    u.id,
    NOW()
FROM announcements a, users u
WHERE u.email = 'anna@realestate.it'
  AND a.titolo IN (
    'Appartamento centrale a Roma',
    'Monolocale in affitto a Milano'
  );

-- =========================================================
-- ANNOUNCEMENT REVIEWS
-- (created_at valorizzato esplicitamente)
-- =========================================================
INSERT INTO announcement_reviews (
    announcement_id,
    user_id,
    rating,
    commento,
    created_at
)
SELECT
    a.id,
    u.id,
    5,
    'Annuncio molto chiaro e immobile interessante!',
    NOW()
FROM announcements a, users u
WHERE u.email = 'anna@realestate.it'
  AND a.titolo = 'Monolocale in affitto a Milano';

-- =========================================================
-- EXTRA PROPERTIES (per comparazione)
-- =========================================================
INSERT INTO properties (
    tipo,
    superficie_mq,
    stanze,
    bagni,
    address,
    city,
    latitude,
    longitude
) VALUES
(
    'MONOLOCALE',
    38,
    1,
    1,
    'Via Torino 12',
    'Milano',
    45.4680,
    9.1810
),
(
    'MONOLOCALE',
    45,
    1,
    1,
    'Via Venezia 8',
    'Milano',
    45.4705,
    9.1950
);

-- =========================================================
-- EXTRA ANNOUNCEMENTS (SIMILI)
-- =========================================================
INSERT INTO announcements (
    titolo,
    descrizione,
    prezzo,
    tipo,
    image_url,
    property_id,
    vendor_id,
    data_pubblicazione
)
SELECT
    'Monolocale moderno in affitto a Milano',
    'Monolocale ristrutturato, vicino alla metro.',
    750,
    'AFFITTO',
    'https://images.unsplash.com/photo-1502673530728-f79b4cab31b1',
    p.id,
    u.id,
    CURRENT_DATE
FROM properties p, users u
WHERE p.address = 'Via Torino 12'
  AND u.email = 'mario@realestate.it';

INSERT INTO announcements (
    titolo,
    descrizione,
    prezzo,
    tipo,
    image_url,
    property_id,
    vendor_id,
    data_pubblicazione
)
SELECT
    'Monolocale arredato in affitto a Milano',
    'Soluzione ideale per studenti e giovani professionisti.',
    680,
    'AFFITTO',
    'https://images.unsplash.com/photo-1522156373667-4c7234bbd804',
    p.id,
    u.id,
    CURRENT_DATE
FROM properties p, users u
WHERE p.address = 'Via Venezia 8'
  AND u.email = 'mario@realestate.it';
