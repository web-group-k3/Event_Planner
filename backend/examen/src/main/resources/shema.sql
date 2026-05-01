-- création base de donnée 
CREATE database EventPlanner;
-- ADMIN (organisateurs authentifiés)
CREATE TABLE admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

-- EVENT
CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    location VARCHAR(255)
);

-- ROOM (liée à un événement)
CREATE TABLE room (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    event_id INT NOT NULL REFERENCES event(id) ON DELETE CASCADE
);

-- SPEAKER (lié à un événement)
CREATE TABLE speaker (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    photo_url TEXT,
    bio TEXT,
    links JSONB,
    event_id INT NOT NULL REFERENCES event(id) ON DELETE CASCADE
);

-- SESSION
CREATE TABLE session (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    capacity INT,
    event_id INT NOT NULL REFERENCES event(id) ON DELETE CASCADE,
    room_id INT REFERENCES room(id) ON DELETE SET NULL
);

-- SESSION_SPEAKER (table d'association N-N)
CREATE TABLE session_speaker (
    session_id INT NOT NULL REFERENCES session(id) ON DELETE CASCADE,
    speaker_id INT NOT NULL REFERENCES speaker(id) ON DELETE CASCADE,
    PRIMARY KEY (session_id, speaker_id)
);

-- QUESTION
CREATE TABLE question (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    author_name VARCHAR(100),  -- nullable = anonyme
    upvotes INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    session_id INT NOT NULL REFERENCES session(id) ON DELETE CASCADE
);