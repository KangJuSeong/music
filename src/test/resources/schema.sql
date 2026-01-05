DROP TABLE IF EXISTS song_features;
DROP TABLE IF EXISTS listening_contexts;
DROP TABLE IF EXISTS song_moods;
DROP TABLE IF EXISTS similar_songs;
DROP TABLE IF EXISTS artists_albums;
DROP TABLE IF EXISTS songs;
DROP TABLE IF EXISTS artists;
DROP TABLE IF EXISTS albums;

CREATE TABLE IF NOT EXISTS albums (
    album_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    album_name VARCHAR(255),
    release_date DATE,
    release_year VARCHAR(4)
);

CREATE TABLE IF NOT EXISTS artists (
    artist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artist_name LONGTEXT
);

CREATE TABLE IF NOT EXISTS songs (
    song_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(255),
    genre VARCHAR(255),
    length VARCHAR(10),
    explicit BOOLEAN DEFAULT FALSE,
    lyrics LONGTEXT,
    artist_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    CONSTRAINT fk_song_artist FOREIGN KEY (artist_id) REFERENCES artists(artist_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_song_album FOREIGN KEY (album_id) REFERENCES albums(album_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS artists_albums (
    split_artist_name VARCHAR(255),
    album_id BIGINT,
    PRIMARY KEY (split_artist_name, album_id),
    CONSTRAINT fk_aa_album FOREIGN KEY (album_id) REFERENCES albums(album_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS song_features (
    feature_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emotion VARCHAR(255),
    key_signature VARCHAR(10),
    time_signature VARCHAR(10),
    tempo DOUBLE,
    loudness DOUBLE,
    song_id BIGINT NOT NULL,
    CONSTRAINT fk_feature_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS song_moods (
    mood_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popularity INT,
    energy INT,
    danceability INT,
    positiveness INT,
    speechiness INT,
    liveness INT,
    acousticness INT,
    instrumentalness INT,
    song_id BIGINT NOT NULL,
    CONSTRAINT fk_mood_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS listen_contexts (
    context_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    party BOOLEAN DEFAULT FALSE,
    work_or_study BOOLEAN DEFAULT FALSE,
    relaxation_or_meditation BOOLEAN DEFAULT FALSE,
    exercise BOOLEAN DEFAULT FALSE,
    running BOOLEAN DEFAULT FALSE,
    yoga_stretching BOOLEAN DEFAULT FALSE,
    driving BOOLEAN DEFAULT FALSE,
    social_gathering BOOLEAN DEFAULT FALSE,
    morning_routine BOOLEAN DEFAULT FALSE,
    song_id BIGINT NOT NULL,
    CONSTRAINT fk_context_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS similar_songs (
    similar_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    similar_artist TEXT NOT NULL,
    similar_song VARCHAR(255) NOT NULL,
    similar_score DOUBLE,
    song_id BIGINT NOT NULL,
    CONSTRAINT fk_similar_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);