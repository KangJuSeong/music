package com.example.music.repository.song;

import com.example.music.dto.SongJsonDto;
import com.example.music.dto.SongResponseDto;
import com.example.music.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SongCustomRepositoryImpl implements SongCustomRepository {
    private final DatabaseClient client;
    private final R2dbcEntityTemplate entityTemplate;

    public SongCustomRepositoryImpl(DatabaseClient client, R2dbcEntityTemplate entityTemplate) {
        this.client = client;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Flux<SongResponseDto> findAllByPageable(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().toList().getFirst();
        String sortBy = order.getProperty();
        String direction = order.getDirection().toString();
        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        String sql = String.format(
                """
                SELECT s.song_id as song_id, s.song_name as song_name, s.length as length, s.genre as genre,
                s.lyrics as lyrics, s.explicit as explicit,
                ar.artist_id as artist_id, ar.artist_name as artist_name,
                al.album_id as album_id, al.album_name as album_name, al.release_date as release_date
                FROM songs s
                JOIN artists ar ON s.artist_id = ar.artist_id
                JOIN albums al ON s.album_id = al.album_id
                WHERE s.song_name IS NOT NULL
                ORDER BY %s %s
                LIMIT :limit OFFSET :offset
                """, sortBy, direction);
        return client.sql(sql)
                .bind("limit", limit)
                .bind("offset", offset)
                .map((row, metadata) -> {
                    SongEntity songEntity = SongEntity.builder()
                            .songId(row.get("song_id", Long.class))
                            .songName(row.get("song_name", String.class))
                            .length(row.get("length", String.class))
                            .genre(row.get("genre", String.class))
                            .lyrics(row.get("lyrics", String.class))
                            .explicit(row.get("explicit", Boolean.class))
                            .artistId(row.get("artist_id", Long.class))
                            .albumId(row.get("album_id", Long.class))
                            .build();
                    ArtistEntity artistEntity = ArtistEntity.builder()
                            .artistId(row.get("artist_id", Long.class))
                            .artistName(row.get("artist_name", String.class))
                            .build();
                    AlbumEntity albumEntity = AlbumEntity.builder()
                            .albumId(row.get("album_id", Long.class))
                            .albumName(row.get("album_name", String.class))
                            .releaseDate(row.get("release_date", LocalDate.class))
                            .build();
                    SongResponseDto songResponseDto = new SongResponseDto();
                    songResponseDto.setSongEntity(songEntity);
                    songResponseDto.setArtistEntity(artistEntity);
                    songResponseDto.setAlbumEntity(albumEntity);
                    return songResponseDto;
                })
                .all();
    }

    @Override
    public Mono<SongResponseDto> findByIdForDetail(Long songId) {
        String sql =
                """
                SELECT s.song_id as song_id, s.song_name as song_name, s.length as length, s.genre as genre,
                s.lyrics as lyrics, s.explicit as explicit,
                
                ar.artist_id as artist_id, ar.artist_name as artist_name,
                
                al.album_id as album_id, al.album_name as album_name, al.release_date as release_date,
                
                sf.feature_id as feature_id, sf.emotion as emotion, sf.key_signature as key_signature,
                sf.time_signature as time_signature, sf.tempo as tempo, sf.loudness as loudness,
                
                sm.mood_id as mood_id, sm.popularity as popularity, sm.energy as energy, sm.danceability as danceability,
                sm.positiveness as positiveness, sm.speechiness as speechiness, sm.liveness as liveness,
                sm.acousticness as acousticness, sm.instrumentalness as instrumentalness,
                
                lc.context_id as context_id, lc.party as party, lc.work_or_study as work_or_study,
                lc.relaxation_or_meditation as relaxation_or_meditation, lc.exercise as exercise, lc.running as running,
                lc.yoga_stretching as yoga_stretching, lc.driving as driving, lc.social_gathering as social_gathering,
                lc.morning_routine as morning_routine,
                
                ss.similar_id as similar_id, ss.similar_song as similar_song, ss.similar_artist as similar_artist,
                ss.similar_score as similar_score
                
                FROM songs s
                JOIN artists ar ON s.artist_id = ar.artist_id
                JOIN albums al ON s.album_id = al.album_id
                JOIN song_features sf ON s.song_id = sf.song_id
                JOIN song_moods sm ON s.song_id = sm.song_id
                JOIN listen_contexts lc ON s.song_id = lc.song_id
                JOIN similar_songs ss ON s.song_id = ss.song_id
                WHERE s.song_id = :songId AND s.song_name IS NOT NULL
                """;
        return client.sql(sql)
                .bind("songId", songId)
                .fetch().all()
                .collectList()
                .map(rows -> {
                    Map<String, Object> firstRow = rows.get(0);
                    List<SimilarSongEntity> similarSongEntities = rows.stream()
                            .map(row -> SimilarSongEntity.builder()
                                    .similarId((Long) row.get("similar_id"))
                                    .similarSong((String) row.get("similar_song"))
                                    .similarArtist((String) row.get("similar_artist"))
                                    .similarScore((Double) row.get("similar_score"))
                                    .build())
                            .toList();
                    SongEntity songEntity = SongEntity.builder()
                            .songId((Long) firstRow.get("song_id"))
                            .songName((String) firstRow.get("song_name"))
                            .length((String) firstRow.get("length"))
                            .genre((String) firstRow.get("genre"))
                            .lyrics((String) firstRow.get("lyrics"))
                            .explicit((Boolean) firstRow.get("explicit"))
                            .artistId((Long) firstRow.get("artist_id"))
                            .albumId((Long) firstRow.get("album_id"))
                            .build();
                    ArtistEntity artistEntity = ArtistEntity.builder()
                            .artistId((Long) firstRow.get("artist_id"))
                            .artistName((String) firstRow.get("artist_name"))
                            .build();
                    AlbumEntity albumEntity = AlbumEntity.builder()
                            .albumId((Long) firstRow.get("album_id"))
                            .albumName((String) firstRow.get("album_name"))
                            .releaseDate((LocalDate) firstRow.get("release_date"))
                            .build();
                    SongFeatureEntity songFeaturesEntity = SongFeatureEntity.builder()
                            .emotion((String) firstRow.get("emotion"))
                            .keySignature((String) firstRow.get("key_signature"))
                            .timeSignature((String) firstRow.get("time_signature"))
                            .tempo((Double) firstRow.get("tempo"))
                            .loudness((Double) firstRow.get("loudness"))
                            .build();
                    SongMoodEntity songMoodsEntity = SongMoodEntity.builder()
                            .moodId((Long) firstRow.get("mood_id"))
                            .popularity((Integer) firstRow.get("popularity"))
                            .energy((Integer) firstRow.get("energy"))
                            .danceability((Integer) firstRow.get("danceability"))
                            .positiveness((Integer) firstRow.get("positiveness"))
                            .speechiness((Integer) firstRow.get("speechiness"))
                            .liveness((Integer) firstRow.get("liveness"))
                            .acousticness((Integer) firstRow.get("acousticness"))
                            .instrumentalness((Integer) firstRow.get("instrumentalness"))
                            .build();
                    ListenContextEntity listenContextsEntity = ListenContextEntity.builder()
                            .contextId((Long) firstRow.get("context_id"))
                            .party((Boolean) firstRow.get("party"))
                            .workOrStudy((Boolean) firstRow.get("work_or_study"))
                            .relaxationOrMeditation((Boolean) firstRow.get("relaxation_or_meditation"))
                            .exercise((Boolean) firstRow.get("exercise"))
                            .running((Boolean) firstRow.get("running"))
                            .yogaStretching((Boolean) firstRow.get("yoga_stretching"))
                            .driving((Boolean) firstRow.get("driving"))
                            .socialGathering((Boolean) firstRow.get("social_gathering"))
                            .morningRoutine((Boolean) firstRow.get("morning_routine"))
                            .build();
                    SongResponseDto songResponseDto = new SongResponseDto();
                    songResponseDto.setSongEntity(songEntity);
                    songResponseDto.setArtistEntity(artistEntity);
                    songResponseDto.setAlbumEntity(albumEntity);
                    songResponseDto.setSongFeatureEntity(songFeaturesEntity);
                    songResponseDto.setSongMoodEntity(songMoodsEntity);
                    songResponseDto.setListenContextEntity(listenContextsEntity);
                    songResponseDto.setSimilarSongEntities(similarSongEntities);
                    return songResponseDto;
                });
    }
}
