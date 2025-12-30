DROP TABLE IF EXISTS songs;
DROP TABLE IF EXISTS song_features;
DROP TABLE IF EXISTS listening_contexts;
DROP TABLE IF EXISTS similar_songs;


CREATE TABLE songs (
    song_id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- PK, 자동 증가
    artist VARCHAR(255) NOT NULL,                -- 가수
    song VARCHAR(255) NOT NULL,                  -- 곡명
    length VARCHAR(10),                          -- 곡 길이 (ex: 03:47)
    emotion VARCHAR(50),                         -- 곡 감성
    genre VARCHAR(50),                           -- 장르
    album VARCHAR(255),                          -- 앨범명
    key_signature VARCHAR(10),                   -- 음높이 (key)
    tempo DOUBLE,                                -- 템포
    loudness DOUBLE,                             -- 데시벨
    time_signature VARCHAR(10),                  -- 박자 (ex: 4/4)
    explicit BOOLEAN,                            -- 노골적인 가사 포함 여부
    release_date DATE,                           -- 발매일
    lyrics TEXT                                  -- 가사
);

CREATE TABLE song_features (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- PK, 자동 증가
    popularity INT,                              -- 인기도 (1~100)
    energy INT,                                  -- 에너지 (1~100)
    danceability INT,                            -- 댄싱 적합성 (1~100)
    positiveness INT,                            -- 긍정도 (1~100)
    speechiness INT,                             -- 구어체 비중 (1~100)
    liveness INT,                                -- 현장감 (1~100)
    acousticness INT,                            -- 어쿠스틱감 (1~100)
    instrumentalness INT,                        -- 연주 비중 (1~100)
    song_id BIGINT NOT NULL,                     -- FK
    CONSTRAINT fk_song_features FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE listening_contexts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,         -- PK
    party BOOLEAN DEFAULT FALSE,                  -- 파티에 적합
    work_study BOOLEAN DEFAULT FALSE,             -- 공부/업무에 적합
    relaxation_meditation BOOLEAN DEFAULT FALSE,  -- 명상에 적합
    exercise BOOLEAN DEFAULT FALSE,               -- 운동에 적합
    running BOOLEAN DEFAULT FALSE,                -- 러닝에 적합
    yoga_stretching BOOLEAN DEFAULT FALSE,        -- 요가/스트레칭에 적합
    driving BOOLEAN DEFAULT FALSE,                -- 드라이브에 적합
    social_gathering BOOLEAN DEFAULT FALSE,       -- 모임에 적합
    morning_routine BOOLEAN DEFAULT FALSE,        -- 아침 루틴에 적합
    song_id BIGINT NOT NULL,                      -- FK
    CONSTRAINT fk_song_listening_contexts FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE similar_songs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,        -- PK
    similar_artist VARCHAR(255) NOT NULL,        -- 유사곡 아티스트
    similar_song VARCHAR(255) NOT NULL,          -- 유사곡 이름
    similar_score DOUBLE,                        -- 유사도
    song_id BIGINT NOT NULL,                     -- FK
    CONSTRAINT fk_song_similar FOREIGN KEY (song_id) REFERENCES songs(song_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);