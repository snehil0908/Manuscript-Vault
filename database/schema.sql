-- ======================================================
-- ManuscriptVault - MySQL Database Setup Script
-- Run this BEFORE starting the Spring Boot application.
-- ======================================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS manuscript_vault
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE manuscript_vault;

-- ======================================================
-- Table: users
-- Stores registered users with hashed passwords and roles.
-- ======================================================
CREATE TABLE IF NOT EXISTS users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,          -- BCrypt hashed
    role     ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    CONSTRAINT chk_username_length CHECK (CHAR_LENGTH(username) >= 3)
);

-- ======================================================
-- Table: manuscripts
-- Stores manuscript metadata.
-- ======================================================
CREATE TABLE IF NOT EXISTS manuscripts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author      VARCHAR(150) NOT NULL,
    date        DATE         NOT NULL,
    description TEXT,
    genre       VARCHAR(100),
    language    VARCHAR(50),
    pages       INT
);

-- ======================================================
-- Seed Data: Admin and sample users
-- Passwords below are BCrypt hashes.
-- admin123  -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- user123   -> $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1OHjinK6O
-- ======================================================
INSERT INTO users (username, password, role) VALUES
    ('admin',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
    ('john_doe', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1OHjinK6O', 'USER'),
    ('jane_doe', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO1OHjinK6O', 'USER');

-- NOTE: If bcrypt hashes above don't work with your Spring version,
-- use the /api/auth/signup endpoint to create users manually after launch.

-- ======================================================
-- Seed Data: Sample manuscripts
-- ======================================================
INSERT INTO manuscripts (title, author, date, description, genre, language, pages) VALUES
(
    'The Lost Chronicles of Ashoka',
    'Ramesh Gupta',
    '1987-03-14',
    'An ancient manuscript discovered in the hills of Odisha, detailing the personal edicts and philosophical musings of Emperor Ashoka after the Kalinga War. Written in Brahmi script on palm leaves, this text offers a rare glimpse into the inner world of one of history''s greatest rulers.',
    'Historical',
    'Sanskrit',
    312
),
(
    'Verses of the Mystic River',
    'Ananya Krishnaswamy',
    '2001-07-22',
    'A collection of 14th-century devotional poetry attributed to a wandering ascetic who traveled the banks of the Ganga. The verses blend themes of love, nature, and spiritual longing in a dialect of Old Hindi that bridges classical Sanskrit and the vernacular.',
    'Poetry',
    'Hindi',
    128
),
(
    'Treatise on Celestial Mechanics',
    'Wilhelm von Hartmann',
    '1743-11-05',
    'A meticulously illustrated manuscript from the Enlightenment era, proposing a unified theory of planetary motion that predated several of Laplace''s formulations. Found in the archives of a Viennese monastery, the work includes hand-drawn orbital diagrams and mathematical proofs.',
    'Science',
    'Latin',
    540
),
(
    'The Merchant''s Secret Ledger',
    'Unknown',
    '1623-09-18',
    'A coded merchant ledger from the Mughal era, deciphered in the 20th century to reveal trade routes between Surat and Hormuz. Contains cipher alphabets, commodity prices, and marginal notes that shed light on 17th-century Indian Ocean commerce.',
    'Historical',
    'Persian',
    87
),
(
    'Songs of the Forgotten Kingdom',
    'Priya Nair',
    '2010-01-30',
    'A contemporary scholarly compilation of oral ballads from the Vijayanagara period, transcribed from memory by elderly singers in Karnataka. The manuscript preserves over 60 compositions that describe court life, festivals, and battles of a lost empire.',
    'Music',
    'Kannada',
    220
),
(
    'Alchemy of the Soul',
    'Brother Bartholomew',
    '1498-06-12',
    'A late medieval European manuscript blending Christian mysticism with early alchemical philosophy. The illuminated text uses allegorical imagery — gold, fire, and rebirth — to describe the spiritual transformation of the soul. Richly decorated with tempera miniatures.',
    'Philosophy',
    'Latin',
    198
);

-- ======================================================
-- Verification Queries (run to confirm setup)
-- ======================================================
SELECT 'Users created:' AS info, COUNT(*) AS count FROM users;
SELECT 'Manuscripts created:' AS info, COUNT(*) AS count FROM manuscripts;

-- ======================================================
-- Useful Admin Queries
-- ======================================================

-- View all users (without passwords):
-- SELECT id, username, role FROM users;

-- View all manuscripts:
-- SELECT id, title, author, date, genre FROM manuscripts ORDER BY date DESC;

-- Search manuscripts:
-- SELECT * FROM manuscripts WHERE title LIKE '%chronicles%' OR author LIKE '%chronicles%';

-- Update a user's role:
-- UPDATE users SET role = 'ADMIN' WHERE username = 'john_doe';

-- Delete a user:
-- DELETE FROM users WHERE username = 'jane_doe';

-- Reset a user password (BCrypt hash of 'newpassword123'):
-- UPDATE users SET password = '$2a$10$...' WHERE username = 'john_doe';
