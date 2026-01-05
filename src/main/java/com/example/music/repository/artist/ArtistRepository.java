package com.example.music.repository.artist;


import com.example.music.entity.ArtistEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ArtistRepository extends ReactiveCrudRepository<ArtistEntity, Long>, ArtistCustomRepository {
    @Query("SELECT COUNT(DISTINCT artist_id) FROM artists_albums")
    Mono<Long> countDistinctArtistForAlbums();
}
