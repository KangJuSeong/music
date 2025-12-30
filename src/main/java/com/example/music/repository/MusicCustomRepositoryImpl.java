package com.example.music.repository;

import com.example.music.dto.MusicJsonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
public class MusicCustomRepositoryImpl implements MusicCustomRepository {
    private final DatabaseClient client;

    public MusicCustomRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    @Transactional
    public Mono<Void> bulkInsert(List<MusicJsonDto> musicJsonDtoList) {
        return Flux.fromIterable(musicJsonDtoList)
                .flatMap(dto -> insertSong(dto)
                        .flatMap(songId ->
                                Mono.when(
                                        insertSongFeature(songId, dto),
                                        insertListeningContext(songId, dto),
                                        insertSimilarSong(songId, dto)
                        ))
                )
                .then();
    }

    private Mono<Long> insertSong(MusicJsonDto dto) {
        String songQuery =
                """
                INSERT INTO songs
                (artist, song, length, emotion, genre, album, key_signature, tempo,
                loudness, time_signature, explicit, release_date, lyrics)
                VALUES
                (:artist, :song, :length, :emotion, :genre, :album, :keySignature, :tempo,
                :loudness, :timeSignature, :explicit, :releaseDate, :lyrics)
                """;
        return Mono.defer(() -> {
            DatabaseClient.GenericExecuteSpec spec = client.sql(songQuery)
                    .filter(s -> s.returnGeneratedValues("song_id"));
            spec = dto.getReleaseDate() == null ?
                    spec.bindNull("releaseDate", LocalDate.class):
                    spec.bind("releaseDate", dto.getReleaseDate());
            spec = dto.getTimeSignature() == null ?
                    spec.bindNull("timeSignature", String.class):
                    spec.bind("timeSignature", dto.getTimeSignature());
            return spec
                .bind("artist", dto.getArtist())
                .bind("song", dto.getSong())
                .bind("length", dto.getLength())
                .bind("emotion", dto.getEmotion())
                .bind("genre", dto.getGenre())
                .bind("album", dto.getAlbum())
                .bind("keySignature", dto.getKeySignature())
                .bind("tempo", dto.getTempo())
                .bind("loudness", dto.getLoudness())
                .bind("explicit", dto.isExplicit())
                .bind("lyrics", dto.getLyrics())
                .fetch()
                .first()
                .map(row -> (Long) row.get("song_id"))
                .doOnNext(songId -> log.debug("Success insert song -> {}", songId));
        })
        .doOnError(th -> log.error("Fail insert song for invalid dto -> {}", dto));
    }

    private Mono<Void> insertSongFeature(Long songId, MusicJsonDto dto) {
        String songFeatureQuery =
                """
                INSERT INTO song_features
                (popularity, energy, danceability, positiveness, speechiness, liveness,
                acousticness, instrumentalness, song_id)
                VALUES
                (:popularity, :energy, :danceability, :positiveness, :speechiness, :liveness,
                :acousticness, :instrumentalness, :songId)
                """;
        return Mono.defer(() -> client.sql(songFeatureQuery)
                    .bind("popularity", dto.getPopularity())
                    .bind("energy", dto.getEnergy())
                    .bind("danceability", dto.getDanceability())
                    .bind("positiveness", dto.getPositiveness())
                    .bind("speechiness", dto.getSpeechiness())
                    .bind("liveness", dto.getLiveness())
                    .bind("acousticness", dto.getAcousticness())
                    .bind("instrumentalness", dto.getInstrumentalness())
                    .bind("songId", songId)
                    .then()
                    .doOnSuccess(Void -> log.debug("Success insert song feature -> {}", songId))
        )
        .doOnError(th -> log.error("Fail insert song feature for invalid dto -> {}", dto));
    }

    private Mono<Void> insertListeningContext(Long songId, MusicJsonDto dto) {
        String listeningContextQuery =
                """
                INSERT INTO listening_contexts
                (party, work_study, relaxation_meditation, exercise, running, yoga_stretching,
                driving, social_gathering, morning_routine, song_id)
                VALUES
                (:party, :workOrStudy, :relaxationOrMeditation, :exercise, :running, :yogaOrStretching,
                :driving, :socialGathering, :morningRoutine, :songId)
                """;
        return Mono.defer(() -> client.sql(listeningContextQuery)
                .bind("party", dto.isParty())
                .bind("workOrStudy", dto.isWorkOrStudy())
                .bind("relaxationOrMeditation", dto.isRelaxationOrMeditation())
                .bind("exercise", dto.isExercise())
                .bind("running", dto.isRunning())
                .bind("yogaOrStretching", dto.isYogaOrStretching())
                .bind("driving", dto.isDriving())
                .bind("socialGathering", dto.isSocialGatherings())
                .bind("morningRoutine", dto.isMorningRoutine())
                .bind("songId", songId)
                .then()
                .doOnSuccess(Void -> log.debug("Success insert listening context -> {}", songId))
        )
        .doOnError(th -> log.error("Fail insert listen context for invalid dto -> {}", dto));
    }

    private Mono<Void> insertSimilarSong(Long songId, MusicJsonDto dto) {
        String similarSongQuery =
                """
                INSERT INTO similar_songs
                (similar_artist, similar_song, similar_score, song_id)
                VALUES
                (:artist, :similarSong, :score, :songId)
                """;
        return Mono.defer(() -> Flux.fromIterable(dto.getSimilarSongs())
                .flatMap(similar -> {
                    DatabaseClient.GenericExecuteSpec spec = client.sql(similarSongQuery);
                    spec = similar.getSimilarSong() == null ?
                            spec.bindNull("similarSong", String.class) :
                            spec.bind("similarSong", similar.getSimilarSong());
                    return spec
                        .bind("artist", similar.getArtist())
                        .bind("score", similar.getScore())
                        .bind("songId", songId)
                        .then();
                },
                3)
                .then()
                .doOnSuccess(Void -> log.debug("Success insert similar song -> {}", songId))
        )
        .doOnError(th -> log.error("Fail insert similar song for invalid dto -> {}", dto));
    }
}
