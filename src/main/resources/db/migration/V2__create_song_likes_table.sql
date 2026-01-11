CREATE TABLE IF NOT EXISTS song_likes (
    song_like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    song_id BIGINT,
    created_at DATETIME,
    CONSTRAINT fk_sl_song FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);