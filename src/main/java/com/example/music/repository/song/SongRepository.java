package com.example.music.repository.song;


import com.example.music.entity.SongEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SongRepository extends ReactiveCrudRepository<SongEntity, Long>, SongCustomRepository {
    @Query("SELECT COUNT(*) FROM songs WHERE song_name IS NOT NULL")
    Mono<Long> countSongs();
}
