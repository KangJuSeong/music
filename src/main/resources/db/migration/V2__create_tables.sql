CREATE TABLE IF NOT EXISTS albums (
    album_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    album_name VARCHAR(300),
    release_date DATE
);

CREATE TABLE IF NOT EXISTS songs (
    song_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    song_name VARCHAR(300),
    genre VARCHAR(100),
    length VARCHAR(10),
    explicit BOOLEAN DEFAULT FALSE,
    lyrics LONGTEXT,
    album_id BIGINT NOT NULL,
    CONSTRAINT fk_song_album FOREIGN KEY (album_id) REFERENCES albums(album_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS artists (
    artist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artist_name VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS artists_albums (
    artist_id BIGINT,
    album_id BIGINT,
    PRIMARY KEY (artist_id, album_id),
    CONSTRAINT fk_aa_artist FOREIGN KEY (artist_id) REFERENCES artists(artist_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_aa_album FOREIGN KEY (album_id) REFERENCES albums(album_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS artists_songs (
    artist_id BIGINT,
    song_id BIGINT,
    PRIMARY KEY (artist_id, song_id),
   CONSTRAINT fk_as_artist FOREIGN KEY (artist_id) REFERENCES artists(artist_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fs_as_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS song_features (
    feature_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emotion VARCHAR(100),
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
    similar_artist VARCHAR(255) NOT NULL,        
    similar_song VARCHAR(255) NOT NULL,          
    similar_score DOUBLE,                        
    song_id BIGINT NOT NULL,                     
    CONSTRAINT fk_similar_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);