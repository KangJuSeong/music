package com.example.music.repository.album;


import com.example.music.entity.AlbumEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AlbumRepository extends ReactiveCrudRepository<AlbumEntity, Long>, AlbumCustomRepository {
    @Query("SELECT COUNT(DISTINCT release_year) FROM albums WHERE release_year IS NOT NULL")
    Mono<Long> countDistinctYearForAlbums();
}
